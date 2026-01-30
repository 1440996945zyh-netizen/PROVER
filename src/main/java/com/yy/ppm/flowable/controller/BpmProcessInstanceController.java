package com.yy.ppm.flowable.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.yy.common.enums.Response;
import com.yy.common.flowable.utils.JsonUtils;
import com.yy.common.flowable.utils.NumberUtils;
import com.yy.common.page.Pages;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.framework.flowable.convert.BpmProcessInstanceConvert;
import com.yy.ppm.flowable.bean.dto.*;
import com.yy.ppm.flowable.bean.po.BpmCategoryPO;
import com.yy.ppm.flowable.bean.po.BpmProcessDefinitionInfoPO;
import com.yy.ppm.flowable.service.BpmCategoryService;
import com.yy.ppm.flowable.service.BpmProcessDefinitionService;
import com.yy.ppm.flowable.service.BpmProcessInstanceService;
import com.yy.ppm.flowable.service.BpmTaskService;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import com.yy.ppm.system.service.SysDeptService;
import com.yy.ppm.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.yy.common.flowable.utils.CollectionUtils.*;
import static com.yy.common.util.SecurityUtils.getLoginUserId;

/**
 * 流程实例
 */
@RestController
@RequestMapping("/bpm/process-instance")
@Validated
public class BpmProcessInstanceController {

    @Resource
    private BpmProcessInstanceService processInstanceService;
    @Resource
    private BpmTaskService taskService;
    @Resource
    private BpmProcessDefinitionService processDefinitionService;
    @Resource
    private BpmCategoryService categoryService;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysDeptService sysDeptService;

    /**
     * 获得我的实例分页列表
     * @param pageReqVO
     * @return
     */
    @GetMapping("/myPage")
    public Map<String,Object> getProcessInstanceMyPage(BpmProcessInstanceSearchDTO pageReqVO) {
        Pages<HistoricProcessInstance> pageResult = processInstanceService.getProcessInstancePage(
                getLoginUserId(), pageReqVO);
        if (CollUtil.isEmpty(pageResult.getPages())) {
            return Response.SUCCESS.newBuilder().toResult(pageResult);
        }

        // 拼接返回
        Map<String, List<Task>> taskMap = taskService.getTaskMapByProcessInstanceIds(
                convertList(pageResult.getPages(), HistoricProcessInstance::getId));
        Map<String, ProcessDefinition> processDefinitionMap = processDefinitionService.getProcessDefinitionMap(
                convertSet(pageResult.getPages(), HistoricProcessInstance::getProcessDefinitionId));
        Map<String, BpmCategoryPO> categoryMap = categoryService.getCategoryMap(
                convertSet(processDefinitionMap.values(), ProcessDefinition::getCategory));
        Map<String, BpmProcessDefinitionInfoPO> processDefinitionInfoMap = processDefinitionService.getProcessDefinitionInfoMap(
                convertSet(pageResult.getPages(), HistoricProcessInstance::getProcessDefinitionId));
        Set<Long> userIds = convertSet(pageResult.getPages(), processInstance -> NumberUtils.parseLong(processInstance.getStartUserId()));
        userIds.addAll(convertSetByFlatMap(taskMap.values(),
                tasks -> tasks.stream().map(Task::getAssignee).filter(StrUtil::isNotBlank).map(Long::parseLong)));
        Map<Long, SysUserDTO> userMap = sysUserService.getUserMap(userIds);
        Map<Long, SysDeptDTO> deptMap = sysDeptService.getDeptMap(
                convertSet(userMap.values(), SysUserDTO::getDeptId));
        Pages<BpmProcessInstanceDTO> bpmProcessInstanceDTOPages = BpmProcessInstanceConvert.INSTANCE.buildProcessInstancePage(pageResult,
                processDefinitionMap, categoryMap, taskMap, userMap, deptMap, processDefinitionInfoMap);
        return Response.SUCCESS.newBuilder().toResult(bpmProcessInstanceDTOPages);
    }

    /**
     * 流程实例列表查询
     * @param pageReqVO
     * @return
     */
    @GetMapping("/manager-page")
    public Map<String,Object> getProcessInstanceManagerPage(BpmProcessInstanceSearchDTO pageReqVO) {
        Pages<HistoricProcessInstance> pageResult = processInstanceService.getProcessInstancePage(
                null, pageReqVO);
        if (CollUtil.isEmpty(pageResult.getPages())) {
            return Response.SUCCESS.newBuilder().toResult(pageResult);
        }

        // 拼接返回
        Map<String, List<Task>> taskMap = taskService.getTaskMapByProcessInstanceIds(
                convertList(pageResult.getPages(), HistoricProcessInstance::getId));
        Map<String, ProcessDefinition> processDefinitionMap = processDefinitionService.getProcessDefinitionMap(
                convertSet(pageResult.getPages(), HistoricProcessInstance::getProcessDefinitionId));
        Map<String, BpmCategoryPO> categoryMap = categoryService.getCategoryMap(
                convertSet(processDefinitionMap.values(), ProcessDefinition::getCategory));
        // 发起人信息
        Map<Long, SysUserDTO> userMap = sysUserService.getUserMap(
                convertSet(pageResult.getPages(), processInstance -> NumberUtils.parseLong(processInstance.getStartUserId())));
        Map<Long, SysDeptDTO> deptMap = sysDeptService.getDeptMap(
                convertSet(userMap.values(), SysUserDTO::getDeptId));
        Map<String, BpmProcessDefinitionInfoPO> processDefinitionInfoMap = processDefinitionService.getProcessDefinitionInfoMap(
                convertSet(pageResult.getPages(), HistoricProcessInstance::getProcessDefinitionId));
        Pages<BpmProcessInstanceDTO> bpmProcessInstanceDTOPages = BpmProcessInstanceConvert.INSTANCE.buildProcessInstancePage(pageResult,
                processDefinitionMap, categoryMap, taskMap, userMap, deptMap, processDefinitionInfoMap);
        return Response.SUCCESS.newBuilder().toResult(bpmProcessInstanceDTOPages);
    }

    /**
     * 新建流程实例
     * @param createReqVO
     * @return
     */
    @PostMapping("/insert")
    @Operation(summary = "")
    @PreAuthorize("@ss.hasPermission('bpm:process-instance:query')")
    public Map<String,Object> createProcessInstance(@RequestBody BpmProcessInstanceDTO createReqVO) {
        ProcessInstance processInstance = processInstanceService.createProcessInstance(getLoginUserId(), createReqVO);
        return Response.SUCCESS.newBuilder().out(StringUtil.isEmpty(processInstance)? "新建失败":"新建成功").toResult();
    }


    /**
     * 获得指定流程实例（在【流程详细】界面中，进行调用）
     * @param id
     * @return
     */
    @GetMapping("/get")
    public Map<String,Object> getProcessInstance(@RequestParam("id") String id) {
        HistoricProcessInstance processInstance = processInstanceService.getHistoricProcessInstance(id);
        // 拼接返回
        ProcessDefinition processDefinition = processDefinitionService.getProcessDefinition(
                processInstance.getProcessDefinitionId());
        BpmProcessDefinitionInfoPO processDefinitionInfo = processDefinitionService.getProcessDefinitionInfo(
                processInstance.getProcessDefinitionId());
        SysUserDTO startUser = sysUserService.getById(NumberUtils.parseLong(processInstance.getStartUserId()));
        SysDeptDTO dept = null;
        if (startUser != null && startUser.getDeptId() != null) {
            dept = sysDeptService.getById(startUser.getDeptId());
        }
        BpmProcessInstanceDTO bpmProcessInstanceDTO = BpmProcessInstanceConvert.INSTANCE.buildProcessInstance(processInstance,
                processDefinition, processDefinitionInfo, startUser, dept);
        return Response.SUCCESS.newBuilder().toResult(bpmProcessInstanceDTO);
    }

    /**
     * 用户取消流程实例
     * @param cancelReqVO
     * @return
     */
    @DeleteMapping("/cancelByStartUser")
    public Map<String,Object> cancelProcessInstanceByStartUser(@RequestBody BpmProcessInstanceCancelDTO cancelReqVO) {
        processInstanceService.cancelProcessInstanceByStartUser(getLoginUserId(), cancelReqVO);
        return Response.SUCCESS.newBuilder().out("取消成功").toResult();
    }

    /**
     * 管理员取消流程实例(管理员撤回流程)
     * @param cancelReqVO
     * @return
     */
    @DeleteMapping("/cancel-by-admin")
    public Map<String,Object> cancelProcessInstanceByManager(
            @Valid @RequestBody BpmProcessInstanceCancelDTO cancelReqVO) {
        processInstanceService.cancelProcessInstanceByAdmin(getLoginUserId(), cancelReqVO);
        return Response.SUCCESS.newBuilder().out("撤回成功").toResult();
    }

    /**
     * 获得审批详情
     * @param reqVO
     * @return
     * 审批前（预测） 通过 getSimulateApproveNodeList  预判流程如果发起，将会经过哪些节点、由谁审批
     * 审批中（追踪） 已完成的节点、进行中的节点、未来待进行的节点
     * 审批后（回溯） 要展示历史审批记录
     */
    @GetMapping("/getApprovalDetail")
    public Map<String,Object> getApprovalDetail(BpmApprovalDetailSearchDTO reqVO) {
        if (StrUtil.isNotEmpty(reqVO.getProcessVariablesStr())) {
            reqVO.setProcessVariables(JsonUtils.parseObject(reqVO.getProcessVariablesStr(), Map.class));
        }
        BpmApprovalDetailDTO approvalDetail = processInstanceService.getApprovalDetail(getLoginUserId(), reqVO);
        return Response.SUCCESS.newBuilder().toResult(approvalDetail);
    }

    /**
     * 获取下一个执行的流程节点
     * @param reqVO
     * @return
     */
    @GetMapping("/get-next-approval-nodes")
    public Map<String,Object> getNextApprovalNodes(BpmApprovalDetailSearchDTO reqVO) {
        if (StrUtil.isNotEmpty(reqVO.getProcessVariablesStr())) {
            reqVO.setProcessVariables(JsonUtils.parseObject(reqVO.getProcessVariablesStr(), Map.class));
        }
        List<BpmApprovalDetailDTO.ActivityNode> nextApprovalNodes = processInstanceService.getNextApprovalNodes(getLoginUserId(), reqVO);
        return Response.SUCCESS.newBuilder().toResult(nextApprovalNodes);
    }

    /**
     * 获取流程实例的 BPMN 模型视图
     * @param id
     * @return
     */
    @GetMapping("/getBpmnModelView")
    public Map<String,Object> getProcessInstanceBpmnModelView(@RequestParam(value = "id") String id) {
        BpmProcessInstanceBpmnModelViewDTO processInstanceBpmnModelView = processInstanceService.getProcessInstanceBpmnModelView(id);
        return Response.SUCCESS.newBuilder().toResult(processInstanceBpmnModelView);
    }

    /**
     * 获得流程实例的打印数据
     * @param processInstanceId
     * @return
     */
    @GetMapping("/get-print-data")
    public Map<String,Object> getProcessInstancePrintData(
            @RequestParam("processInstanceId") String processInstanceId) {
        HistoricProcessInstance historicProcessInstance = processInstanceService.getHistoricProcessInstance(processInstanceId);
        if (historicProcessInstance == null) {
            throw new BusinessRuntimeException("流程实例不存在");
        }
        SysUserDTO startUser = sysUserService.getById(Long.valueOf(historicProcessInstance.getStartUserId()));
        SysDeptDTO dept = sysDeptService.getById(startUser.getDeptId());
        List<HistoricTaskInstance> tasks = taskService.getFinishedTaskListByProcessInstanceIdWithoutCancel(processInstanceId);
        Map<Long, SysUserDTO> userMap = sysUserService.getUserMap(
                convertSet(tasks, item -> Long.valueOf(item.getAssignee())));
        BpmProcessPrintDataDTO bpmProcessPrintDataDTO = BpmProcessInstanceConvert.INSTANCE.buildProcessInstancePrintData(historicProcessInstance,
                processDefinitionService.getProcessDefinitionInfo(historicProcessInstance.getProcessDefinitionId()),
                tasks, userMap,
                new UserSimpleBaseDTO().setUserName(startUser.getUserName()).setDeptName(dept.getDeptName()));
        return Response.SUCCESS.newBuilder().toResult(bpmProcessPrintDataDTO);
    }

}
