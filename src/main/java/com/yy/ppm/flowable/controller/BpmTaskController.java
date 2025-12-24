package com.yy.ppm.flowable.controller;

import cn.hutool.core.collection.CollUtil;
import com.yy.common.enums.Response;
import com.yy.common.flowable.utils.NumberUtils;
import com.yy.common.page.Pages;
import com.yy.framework.flowable.convert.BpmTaskConvert;
import com.yy.ppm.flowable.bean.dto.*;
import com.yy.ppm.flowable.bean.po.BpmFormPO;
import com.yy.ppm.flowable.bean.po.BpmProcessDefinitionInfoPO;
import com.yy.ppm.flowable.service.BpmFormService;
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
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.yy.common.flowable.utils.CollectionUtils.*;
import static com.yy.common.util.SecurityUtils.getLoginUserId;

/**
 * 流程任务实例
 */
@RestController
@RequestMapping("/bpm/task")
@Validated
public class BpmTaskController {

    @Resource
    private BpmTaskService taskService;
    @Resource
    private BpmProcessInstanceService processInstanceService;
    @Resource
    private BpmFormService formService;
    @Resource
    private BpmProcessDefinitionService processDefinitionService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private SysDeptService sysDeptService;


    /**
     * 待办任务分页
     * @param pageVO
     * @return
     */
    @GetMapping("todoPage")
    public Map<String,Object> getTaskTodoPage(BpmTaskSearchDTO pageVO) {
        Pages<Task> pageResult = taskService.getTaskTodoPage(getLoginUserId(), pageVO);
        if (CollUtil.isEmpty(pageResult.getPages())) {
            return Response.SUCCESS.newBuilder().toResult(pageResult);
        }

        // 拼接数据
        Map<String, ProcessInstance> processInstanceMap = processInstanceService.getProcessInstanceMap(
                convertSet(pageResult.getPages(), Task::getProcessInstanceId));
        Map<Long, SysUserDTO> userMap = sysUserService.getUserMap(
                convertSet(processInstanceMap.values(), instance -> Long.valueOf(instance.getStartUserId())));
        Map<String, BpmProcessDefinitionInfoPO> processDefinitionInfoMap = processDefinitionService.getProcessDefinitionInfoMap(
                convertSet(pageResult.getPages(), Task::getProcessDefinitionId));
        Pages<BpmTaskDTO> bpmTaskDTOPages = BpmTaskConvert.INSTANCE.buildTodoTaskPage(pageResult, processInstanceMap, userMap, processDefinitionInfoMap);
        return Response.SUCCESS.newBuilder().toResult(bpmTaskDTOPages);
    }

    /**
     * 获取 Done 已办任务分页
     * @param pageVO
     * @return
     */
    @GetMapping("donePage")
    public Map<String,Object> getTaskDonePage(BpmTaskSearchDTO pageVO) {
        Pages<HistoricTaskInstance> pageResult = taskService.getTaskDonePage(getLoginUserId(), pageVO);
        if (CollUtil.isEmpty(pageResult.getPages())) {
            return Response.SUCCESS.newBuilder().toResult(pageResult);
        }

        // 拼接数据
        Map<String, HistoricProcessInstance> processInstanceMap = processInstanceService.getHistoricProcessInstanceMap(
                convertSet(pageResult.getPages(), HistoricTaskInstance::getProcessInstanceId));
        Map<Long, SysUserDTO> userMap = sysUserService.getUserMap(
                convertSet(processInstanceMap.values(), instance -> Long.valueOf(instance.getStartUserId())));
        Map<String, BpmProcessDefinitionInfoPO> processDefinitionInfoMap = processDefinitionService.getProcessDefinitionInfoMap(
                convertSet(pageResult.getPages(), HistoricTaskInstance::getProcessDefinitionId));
        Pages<BpmTaskDTO> bpmTaskDTOPages = BpmTaskConvert.INSTANCE.buildTaskPage(pageResult, processInstanceMap, userMap, null, processDefinitionInfoMap);
        return Response.SUCCESS.newBuilder().toResult(bpmTaskDTOPages);
    }

    /**
     * 获取全部任务的分页
     * @param pageVO
     * @return
     */
    @GetMapping("managerPage")
    public Map<String,Object> getTaskManagerPage(BpmTaskSearchDTO pageVO) {
        Pages<HistoricTaskInstance> pageResult = taskService.getTaskPage(getLoginUserId(), pageVO);
        if (CollUtil.isEmpty(pageResult.getPages())) {
            return Response.SUCCESS.newBuilder().toResult(pageResult);
        }

        // 拼接数据
        Map<String, HistoricProcessInstance> processInstanceMap = processInstanceService.getHistoricProcessInstanceMap(
                convertSet(pageResult.getPages(), HistoricTaskInstance::getProcessInstanceId));
        // 获得 User 和 Dept Map
        Set<Long> userIds = convertSet(processInstanceMap.values(), instance -> Long.valueOf(instance.getStartUserId()));
        userIds.addAll(convertSet(pageResult.getPages(), task -> NumberUtils.parseLong(task.getAssignee())));
        Map<Long, SysUserDTO> userMap = sysUserService.getUserMap(userIds);
        Map<Long, SysDeptDTO> deptMap = sysDeptService.getDeptMap(
                convertSet(userMap.values(), SysUserDTO::getDeptId));
        Map<String, BpmProcessDefinitionInfoPO> processDefinitionInfoMap = processDefinitionService.getProcessDefinitionInfoMap(
                convertSet(pageResult.getPages(), HistoricTaskInstance::getProcessDefinitionId));
        Pages<BpmTaskDTO> bpmTaskDTOPages = BpmTaskConvert.INSTANCE.buildTaskPage(pageResult, processInstanceMap, userMap, deptMap, processDefinitionInfoMap);
        return Response.SUCCESS.newBuilder().toResult(bpmTaskDTOPages);
    }

    /**
     * 获得指定流程实例的任务列表(包括完成的、未完成的)
     * @param processInstanceId
     * @return
     */
    @GetMapping("/getByProcessInstanceId")
    public Map<String,Object> getTaskListByProcessInstanceId(@RequestParam("processInstanceId") String processInstanceId) {
        List<HistoricTaskInstance> taskList = taskService.getTaskListByProcessInstanceId(processInstanceId, true);
        if (CollUtil.isEmpty(taskList)) {
            return Response.SUCCESS.newBuilder().toResult(taskList);
        }

        // 拼接数据
        Set<Long> userIds = convertSetByFlatMap(taskList, task ->
                Stream.of(NumberUtils.parseLong(task.getAssignee()), NumberUtils.parseLong(task.getOwner())));
        Map<Long, SysUserDTO> userMap = sysUserService.getUserMap(userIds);
        Map<Long, SysDeptDTO> deptMap = sysDeptService.getDeptMap(
                convertSet(userMap.values(), SysUserDTO::getDeptId));
        // 获得 Form Map
        Map<Long, BpmFormPO> formMap = formService.getFormMap(
                convertSet(taskList, task -> NumberUtils.parseLong(task.getFormKey())));
        List<BpmTaskDTO> bpmTaskDTOS = BpmTaskConvert.INSTANCE.buildTaskListByProcessInstanceId(taskList,
                formMap, userMap, deptMap);
        return Response.SUCCESS.newBuilder().toResult(bpmTaskDTOS);
    }

    /**
     * 通过任务
     * @param reqVO
     * @return
     */
    @PutMapping("/approve")
    public Map<String,Object> approveTask( @RequestBody BpmTaskApproveDTO reqVO) {
        taskService.approveTask(getLoginUserId(), reqVO);
        return Response.SUCCESS.newBuilder().out("处理成功").toResult();
    }

    /**
     * 不通过任务
     * @param reqVO
     * @return
     */
    @PutMapping("/reject")
    public Map<String,Object> rejectTask(@RequestBody BpmTaskRejectDTO reqVO) {
        taskService.rejectTask(getLoginUserId(), reqVO);
        return Response.SUCCESS.newBuilder().out("处理成功").toResult();
    }

    /**
     * 获取所有可退回的节点(用于【流程详情】的【退回】按钮)
     * @param id
     * @return
     */
    @GetMapping("/list-by-return")
    public Map<String,Object> getTaskListByReturn(@RequestParam("id") String id) {
        List<UserTask> userTaskList = taskService.getUserTaskListByReturn(id);
        List<BpmTaskDTO> bpmTaskDTOS = convertList(userTaskList, userTask -> // 只返回 id 和 name
                new BpmTaskDTO().setName(userTask.getName()).setTaskDefinitionKey(userTask.getId()));
        return Response.SUCCESS.newBuilder().toResult(bpmTaskDTOS);
    }

    /**
     * 退回任务(用于【流程详情】的【退回】按钮)
     * @param reqVO
     * @return
     */
    @PutMapping("/return")
    public Map<String,Object> returnTask(@RequestBody BpmTaskReturnDTO reqVO) {
        taskService.returnTask(getLoginUserId(), reqVO);
        return Response.SUCCESS.newBuilder().out("处理成功").toResult();
    }

    /**
     * 委派任务(用于【流程详情】的【委派】按钮)
     * @param reqVO
     * @return
     */
    @PutMapping("/delegate")
    public Map<String,Object> delegateTask(@RequestBody BpmTaskDelegateDTO reqVO) {
        taskService.delegateTask(getLoginUserId(), reqVO);
        return Response.SUCCESS.newBuilder().out("处理成功").toResult();
    }

    /**
     * 转派任务(用于【流程详情】的【转派】按钮)
     * @param reqVO
     * @return
     */
    @PutMapping("/transfer")
    public Map<String,Object> transferTask(@RequestBody BpmTaskTransferDTO reqVO) {
        taskService.transferTask(getLoginUserId(), reqVO);
        return Response.SUCCESS.newBuilder().out("处理成功").toResult();
    }

    /**
     * 加签(before 前加签，after 后加签)
     * @param reqVO
     * @return
     */
    @PutMapping("/createSign")
    public Map<String,Object> createSignTask(@RequestBody BpmTaskSignCreateDTO reqVO) {
        taskService.createSignTask(getLoginUserId(), reqVO);
        return Response.SUCCESS.newBuilder().out("处理成功").toResult();
    }

    /**
     * 减签
     * @param reqVO
     * @return
     */
    @DeleteMapping("/delete-sign")
    public Map<String,Object> deleteSignTask(@Valid @RequestBody BpmTaskSignDeleteDTO reqVO) {
        taskService.deleteSignTask(getLoginUserId(), reqVO);
        return Response.SUCCESS.newBuilder().out("处理成功").toResult();
    }

    /**
     * 抄送任务
     * @param reqVO
     * @return
     */
    @PutMapping("/copy")
    public Map<String,Object> copyTask(@RequestBody BpmTaskCopyDTO reqVO) {
        taskService.copyTask(getLoginUserId(), reqVO);
        return Response.SUCCESS.newBuilder().out("处理成功").toResult();
    }

    /**
     * 撤回任务
     * @param taskId
     * @return
     */
    @PutMapping("/withdraw")
    public Map<String,Object> withdrawTask(@RequestParam("taskId") String taskId) {
        taskService.withdrawTask(getLoginUserId(), taskId);
        return Response.SUCCESS.newBuilder().out("处理成功").toResult();
    }

    /**
     * 获得指定父级任务的子任务列表(目前用于，减签的时候，获得子任务列表)
     * @param parentTaskId
     * @return
     */
    @GetMapping("/list-by-parent-task-id")
    public Map<String,Object> getTaskListByParentTaskId(@RequestParam("parentTaskId") String parentTaskId) {
        List<Task> taskList = taskService.getTaskListByParentTaskId(parentTaskId);
        if (CollUtil.isEmpty(taskList)) {
            return Response.SUCCESS.newBuilder().toResult(taskList);
        }
        // 拼接数据
        Map<Long, SysUserDTO> userMap = sysUserService.getUserMap(convertSetByFlatMap(taskList,
                user -> Stream.of(NumberUtils.parseLong(user.getAssignee()), NumberUtils.parseLong(user.getOwner()))));
        Map<Long, SysDeptDTO> deptMap = sysDeptService.getDeptMap(
                convertSet(userMap.values(), SysUserDTO::getDeptId));
        List<BpmTaskDTO> bpmTaskDTOS = BpmTaskConvert.INSTANCE.buildTaskListByParentTaskId(taskList, userMap, deptMap);
        return Response.SUCCESS.newBuilder().toResult(bpmTaskDTOS);
    }

}
