package com.yy.ppm.flowable.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.PageResult;
import com.yy.common.flowable.constants.BpmnModelConstants;
import com.yy.common.flowable.utils.BeanUtils;
import com.yy.common.flowable.utils.FlowableUtils;
import com.yy.common.page.Pages;
import com.yy.common.util.PageConverterUtils;
import com.yy.ppm.flowable.bean.dto.BpmModelMetaInfoDTO;
import com.yy.ppm.flowable.bean.dto.BpmProcessDefinitionInfoSearchDTO;
import com.yy.ppm.flowable.bean.po.BpmFormPO;
import com.yy.ppm.flowable.bean.po.BpmProcessDefinitionInfoPO;
import com.yy.ppm.flowable.mapper.BpmProcessDefinitionInfoMapper;
import com.yy.ppm.flowable.service.BpmProcessDefinitionService;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import com.yy.ppm.system.service.SysUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.impl.db.SuspensionState;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.util.*;

import static com.yy.common.flowable.constants.ErrorCodeConstants.*;
import static com.yy.common.flowable.utils.CollectionUtils.addIfNotNull;
import static com.yy.common.flowable.utils.ServiceExceptionUtil.exception;
import static java.util.Collections.emptyList;

/**
 * 流程定义实现
 * 主要进行 Flowable {@link ProcessDefinition} 和 {@link Deployment} 的维护
 *
 * @author yunlongn
 * @author ZJQ
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class BpmProcessDefinitionServiceImpl implements BpmProcessDefinitionService {

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private BpmProcessDefinitionInfoMapper processDefinitionMapper;

    @Resource
    private SysUserService sysUserService;

    @Autowired
    private Snowflake snowflake;

    /**
     * 获得流程定义编号对应的 ProcessDefinition
     *
     * @param id 流程定义编号
     * @return 流程定义
     */
    @Override
    public ProcessDefinition getProcessDefinition(String id) {
        return repositoryService.getProcessDefinition(id);
    }

    @Override
    public List<ProcessDefinition> getProcessDefinitionList(Set<String> ids) {
        return repositoryService.createProcessDefinitionQuery().processDefinitionIds(ids).list();
    }

    @Override
    public ProcessDefinition getProcessDefinitionByDeploymentId(String deploymentId) {
        if (StrUtil.isEmpty(deploymentId)) {
            return null;
        }
        return repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
    }

    @Override
    public List<ProcessDefinition> getProcessDefinitionListByDeploymentIds(Set<String> deploymentIds) {
        if (CollUtil.isEmpty(deploymentIds)) {
            return emptyList();
        }
        return repositoryService.createProcessDefinitionQuery().deploymentIds(deploymentIds).list();
    }

    /**
     * 获得流程定义标识对应的激活的流程定义
     *
     * @param key 流程定义的标识
     * @return 流程定义
     */
    @Override
    public ProcessDefinition getActiveProcessDefinition(String key) {
        return repositoryService.createProcessDefinitionQuery()
//                .processDefinitionTenantId(FlowableUtils.getTenantId())
                .processDefinitionKey(key).active().singleResult();
    }

    /**
     * 判断用户是否可以使用该流程定义，进行流程的发起
     *
     * @param processDefinition 流程定义
     * @param userId 用户编号
     * @return 是否可以发起流程
     */
    @Override
    public boolean canUserStartProcessDefinition(BpmProcessDefinitionInfoPO processDefinition, Long userId) {
        if (processDefinition == null) {
            return false;
        }

        // 校验用户是否在允许发起的用户列表中
        if (CollUtil.isNotEmpty(processDefinition.getStartUserIds())) {
            return processDefinition.getStartUserIds().contains(userId);
        }

        // 校验用户是否在允许发起的部门列表中
        if (CollUtil.isNotEmpty(processDefinition.getStartDeptIds())) {
            SysUserDTO user = sysUserService.getById(userId);
            return user != null
                    && user.getDeptId() != null
                    && processDefinition.getStartDeptIds().contains(user.getDeptId());
        }

        // 都为空，则所有人都可以发起
        return true;
    }

    @Override
    public List<Deployment> getDeploymentList(Set<String> ids) {
        if (CollUtil.isEmpty(ids)) {
            return emptyList();
        }
        List<Deployment> list = new ArrayList<>(ids.size());
        for (String id : ids) {
            addIfNotNull(list, getDeployment(id));
        }
        return list;
    }

    @Override
    public Deployment getDeployment(String id) {
        if (StrUtil.isEmpty(id)) {
            return null;
        }
        return repositoryService.createDeploymentQuery().deploymentId(id).singleResult();
    }

    @Override
    public String createProcessDefinition(Model model, BpmModelMetaInfoDTO modelMetaInfo,
                                          byte[] bpmnBytes, BpmFormPO form) {
        // 创建 Deployment 部署
        Deployment deploy = repositoryService.createDeployment()
                .key(model.getKey()).name(model.getName()).category(model.getCategory())
                .addBytes(model.getKey() + BpmnModelConstants.BPMN_FILE_SUFFIX, bpmnBytes)
                // 租户禁用
//                .tenantId(FlowableUtils.getTenantId())
                .disableSchemaValidation() // 禁用 XML Schema 验证，因为有自定义的属性
                .deploy();

        // 设置 ProcessDefinition 的 category 分类
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deploy.getId()).singleResult();
        repositoryService.setProcessDefinitionCategory(definition.getId(), model.getCategory());
        // 注意 1，ProcessDefinition 的 key 和 name 是通过 BPMN 中的 <bpmn2:process /> 的 id 和 name 决定
        // 注意 2，目前该项目的设计上，需要保证 Model、Deployment、ProcessDefinition 使用相同的 key，保证关联性。
        //          否则，会导致 ProcessDefinition 的分页无法查询到。
        if (!Objects.equals(definition.getKey(), model.getKey())) {
            throw exception(PROCESS_DEFINITION_KEY_NOT_MATCH, model.getKey(), definition.getKey());
        }
        if (!Objects.equals(definition.getName(), model.getName()))
            throw exception(PROCESS_DEFINITION_NAME_NOT_MATCH, model.getName(), definition.getName());

        // 插入拓展表
        BpmProcessDefinitionInfoPO definitionDO = BeanUtils.toBean(modelMetaInfo, BpmProcessDefinitionInfoPO.class)
                .setModelId(model.getId()).setCategory(model.getCategory()).setProcessDefinitionId(definition.getId())
                .setModelType(modelMetaInfo.getType());
        if (form != null) {
            definitionDO.setFormFields(form.getFields()).setFormConf(form.getConf());
        }
        definitionDO.setId(snowflake.nextId());
        processDefinitionMapper.insert(definitionDO);
        return definition.getId();
    }

    @Override
    public void updateProcessDefinitionState(String id, Integer state) {
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(id);
        if (processDefinition == null) {
            throw exception(PROCESS_DEFINITION_NOT_EXISTS);
        }

        // 激活
        if (Objects.equals(SuspensionState.ACTIVE.getStateCode(), state)) {
            if (processDefinition.isSuspended()) {
                repositoryService.activateProcessDefinitionById(id, false, null);
            }
            return;
        }
        // 挂起
        if (Objects.equals(SuspensionState.SUSPENDED.getStateCode(), state)) {
            // suspendProcessInstances = false，进行中的任务，不进行挂起。
            // 原因：只要新的流程不允许发起即可，老流程继续可以执行。
            if (!processDefinition.isSuspended()) {
                repositoryService.suspendProcessDefinitionById(id, false, null);
            }
            return;
        }
        log.error("[updateProcessDefinitionState][流程定义({}) 修改未知状态({})]", id, state);
    }

    /**
     * 获得流程定义对应的 BPMN
     *
     * @param id 流程定义编号
     * @return BPMN
     */
    @Override
    public BpmnModel getProcessDefinitionBpmnModel(String id) {
        return repositoryService.getBpmnModel(id);
    }

    /**
     * 获得流程定义的信息
     *
     * @param id 流程定义编号
     * @return 流程定义信息
     */
    @Override
    public BpmProcessDefinitionInfoPO getProcessDefinitionInfo(String id) {
        Collection<String> idList = new ArrayList<>();
        idList.add(id);
        return processDefinitionMapper.selectListByProcessDefinitionIds(idList).get(0);
    }

    /**
     * 获得流程定义的信息 List
     *
     * @param ids 流程定义编号数组
     * @return 流程额定义信息数组
     */
    @Override
    public List<BpmProcessDefinitionInfoPO> getProcessDefinitionInfoList(Collection<String> ids) {
        return processDefinitionMapper.selectListByProcessDefinitionIds(ids);
    }

    /**
     * 获得流程定义分页
     *
     * @param pageVO 分页入参
     * @return 流程定义 Page
     */
    @Override
    public Pages<ProcessDefinition> getProcessDefinitionPage(BpmProcessDefinitionInfoSearchDTO pageVO) {
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
//        query.processDefinitionTenantId(FlowableUtils.getTenantId());
        if (StrUtil.isNotBlank(pageVO.getKey())) {
            query.processDefinitionKey(pageVO.getKey());
        }
        // 执行查询
        long count = query.count();
        int startData = (pageVO.getStartPage() - 1) * pageVO.getPageSize();
        List<ProcessDefinition> list = query.orderByProcessDefinitionVersion().desc()
                .listPage(startData,pageVO.getPageSize());
        return PageConverterUtils.convert(list,pageVO.getStartPage(),pageVO.getPageSize(),count);
    }

    @Override
    public List<ProcessDefinition> getProcessDefinitionListBySuspensionState(Integer suspensionState) {
        // 拼接查询条件
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
        if (Objects.equals(SuspensionState.SUSPENDED.getStateCode(), suspensionState)) {
            query.suspended();
        } else if (Objects.equals(SuspensionState.ACTIVE.getStateCode(), suspensionState)) {
            query.active();
        }
        // 执行查询
//        query.processDefinitionTenantId(FlowableUtils.getTenantId());
        return query.list();
    }

}
