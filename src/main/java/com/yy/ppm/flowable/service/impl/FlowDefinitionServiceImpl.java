package com.yy.ppm.flowable.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.yy.common.page.Pages;
import com.yy.ppm.flowable.bean.dto.FlowProcDefDto;
import com.yy.ppm.flowable.bean.po.SysCustomForm;
import com.yy.ppm.flowable.bean.po.SysDeployForm;
import com.yy.ppm.flowable.bean.po.SysForm;
import com.yy.ppm.flowable.factory.FlowServiceFactory;
import com.yy.ppm.flowable.mapper.SysFormMapper;
import com.yy.ppm.flowable.service.IFlowDefinitionService;
import com.yy.ppm.flowable.service.ISysDeployFormService;
import com.yy.ppm.flowable.util.FlowableUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.*;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @Description: 流程定义
 * @Author: hukang
 * @Date:   2025-11-07
 * @Version: V1.0
 */
@Slf4j
@Service
public class FlowDefinitionServiceImpl implements IFlowDefinitionService {
    @Resource
    private SysFormMapper sysFormMapper;

    @Resource
    private ISysDeployFormService sysDeployFormService;

    @Resource
    private FlowServiceFactory flowServiceFactory;

    // Flowable服务获取方法
    protected RepositoryService getRepositoryService() {
        return flowServiceFactory.getRepositoryService();
    }
    private static final String BPMN_FILE_SUFFIX = ".bpmn";

    /**
     * 导入流程文件
     *
     * @param name
     * @param category
     * @param in
     */
    @Override
    public String importFile(String name, String category, InputStream in) {
        Deployment deploy = getRepositoryService().createDeployment()
                .addInputStream(name + BPMN_FILE_SUFFIX, in)
                .name(name)
                .category(category)
                .deploy();
        ProcessDefinition definition = getRepositoryService().createProcessDefinitionQuery()
                .deploymentId(deploy.getId())
                .singleResult();
        getRepositoryService().setProcessDefinitionCategory(definition.getId(), category);
        return deploy.getId();
    }

    /**
     * 新增流程实例关联表单
     *
     * @param deployId,bpmnModel 流程实例关联表单
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveDeployForm(String deployId, BpmnModel bpmnModel) {
        List<SysDeployForm> deployFormList = new ArrayList<>();
        // 获取开始节点
        StartEvent startEvent = FlowableUtils.getStartEvent(bpmnModel);
        if (ObjectUtil.isNull(startEvent)) {
            throw new RuntimeException("开始节点不存在，请检查流程设计是否有误！");
        }
        // 保存开始节点表单信息
        SysDeployForm startDeployForm = buildDeployForm(deployId, startEvent);
        if (ObjectUtil.isNotNull(startDeployForm)) {
            deployFormList.add(startDeployForm);
        }
        // 保存用户节点表单信息
        Collection<UserTask> userTasks = FlowableUtils.getAllUserTaskEvent(bpmnModel);
        if (CollUtil.isNotEmpty(userTasks)) {
            for (UserTask userTask : userTasks) {
                SysDeployForm userTaskDeployForm = buildDeployForm(deployId, userTask);
                if (ObjectUtil.isNotNull(userTaskDeployForm)) {
                    deployFormList.add(userTaskDeployForm);
                }
            }
        }
        // 批量新增部署流程和表单关联信息
        return sysDeployFormService.insertBatch(deployFormList);
    }
    /**
     * 列表查询
     */
    @Override
    public Pages<FlowProcDefDto> getList(FlowProcDefDto flowProcDefDto) {
        Pages<FlowProcDefDto> pages = new Pages<>();
        // 流程定义列表数据查询
        ProcessDefinitionQuery processDefinitionQuery = getRepositoryService().createProcessDefinitionQuery();
        processDefinitionQuery
                .latestVersion()   //获取最新的一个版本
                .orderByProcessDefinitionName().asc();
        /*=====参数=====*/
        if (StrUtil.isNotBlank(flowProcDefDto.getName())){
            processDefinitionQuery.processDefinitionNameLike("%"+flowProcDefDto.getName()+"%");
        }
        if (StrUtil.isNotBlank(flowProcDefDto.getCategory())){
            processDefinitionQuery.processDefinitionCategory(flowProcDefDto.getCategory());
        }
        if (flowProcDefDto.getSuspensionState() == 1){
            processDefinitionQuery.active();
        }
        /*============*/
        pages.setTotalNum(processDefinitionQuery.count());
        //获取分页数据
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.listPage((flowProcDefDto.getStartPage() - 1) * flowProcDefDto.getPageSize(), flowProcDefDto.getPageSize());
        List<FlowProcDefDto> dataList = new ArrayList<>();
        for (ProcessDefinition processDefinition : processDefinitionList) {
            String deploymentId = processDefinition.getDeploymentId();
            Deployment deployment = getRepositoryService().createDeploymentQuery()
                    .deploymentId(deploymentId)
                    .singleResult();
            FlowProcDefDto reProcDef = new FlowProcDefDto();
            BeanUtils.copyProperties(processDefinition, reProcDef);
            // 流程定义时间
            reProcDef.setDeploymentTime(deployment.getDeploymentTime());
            // 系统流程表单
            SysForm sysForm = sysDeployFormService.selectSysDeployFormByDeployId(reProcDef.getDeploymentId());
            if (Objects.nonNull(sysForm)) {
                reProcDef.setFormName(sysForm.getFormName());
                reProcDef.setFormId(sysForm.getId());
            }
            // 系统自定义表单表
            SysCustomForm sysCustomForm = sysDeployFormService.selectSysCustomFormByDeployId(reProcDef.getDeploymentId());
            if (Objects.nonNull(sysCustomForm)) {
                reProcDef.setFormName(sysCustomForm.getBusinessName());
                reProcDef.setFormId(sysCustomForm.getId());
            }
            // online表单
//            FlowDeployOnline flowDeployOnline = iFlowDeployOnlineService.selectFlowDeployOnlineByDeployId(reProcDef.getDeploymentId());
//            if (Objects.nonNull(flowDeployOnline)) {
//                reProcDef.setFormName(flowDeployOnline.getTableName());
//                reProcDef.setFormId(flowDeployOnline.getId());
//            }
            dataList.add(reProcDef);
        }
        pages.setPages(dataList);
        return pages;
    }

    /**
     * 获取流程图（读取xml）
     */
    @Override
    public String readXml(String deployId) throws IOException {
        ProcessDefinition definition = getRepositoryService().createProcessDefinitionQuery().deploymentId(deployId).singleResult();
        InputStream inputStream = getRepositoryService().getResourceAsStream(definition.getDeploymentId(), definition.getResourceName());
        return IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
    }

    /**
     * 激活或挂起流程
     */
    @Override
    public void updateState(Integer state, String deployId) {
        ProcessDefinition procDef = getRepositoryService().createProcessDefinitionQuery().deploymentId(deployId).singleResult();
        // 激活
        if (state == 1) {
            getRepositoryService().activateProcessDefinitionById(procDef.getId(), true, null);
        }
        // 挂起
        if (state == 2) {
            getRepositoryService().suspendProcessDefinitionById(procDef.getId(), true, null);
        }
    }

    /**
     * 删除流程
     * @param deployId
     * @return
     */
    @Override
    public void delete(String deployId) {
        getRepositoryService().deleteDeployment(deployId, true);
    }

    /**
     * 构建发布表单关联信息对象
     * @param deployId 部署ID
     * @param node 节点信息
     * @return 发布表单关联对象。若无表单信息（formKey），则返回null
     */
    private SysDeployForm buildDeployForm(String deployId, FlowNode node) {
        String formKey = null;
        SysDeployForm deployForm = new SysDeployForm();
        if (node instanceof StartEvent) {
            formKey = ((StartEvent) node).getFormKey();
            deployForm.setFormFlag("1"); //作为开始form表单标志
        } else if (node instanceof UserTask) {
            formKey = ((UserTask) node).getFormKey();
        }
        if (StringUtils.isEmpty(formKey)) {
            return null;
        }
        SysForm sysForm = sysFormMapper.selectSysFormById(formKey);
        if (ObjectUtil.isNull(sysForm)) {
            throw new RuntimeException("表单信息查询错误");
        }
        deployForm.setDeployId(deployId);
        deployForm.setNodeKey(node.getId());
        deployForm.setNodeName(node.getName());
        deployForm.setFormId(formKey);
        return deployForm;
    }
}
