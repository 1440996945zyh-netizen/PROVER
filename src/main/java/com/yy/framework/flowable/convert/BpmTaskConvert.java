package com.yy.framework.flowable.convert;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.yy.common.flowable.common.KeyValue;
import com.yy.common.flowable.enums.BpmTaskStatusEnum;
import com.yy.common.flowable.utils.*;
import com.yy.common.page.Pages;
import com.yy.common.util.PageConverterUtils;
import com.yy.ppm.flowable.bean.dto.BpmTaskDTO;
import com.yy.ppm.flowable.bean.dto.UserSimpleBaseDTO;
import com.yy.ppm.flowable.bean.po.BpmFormPO;
import com.yy.ppm.flowable.bean.po.BpmProcessDefinitionInfoPO;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.yy.common.flowable.utils.CollectionUtils.convertList;
import static com.yy.common.flowable.utils.MapUtils.findAndThen;

/**
 * Bpm 任务 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface BpmTaskConvert {

    BpmTaskConvert INSTANCE = Mappers.getMapper(BpmTaskConvert.class);

    default Pages<BpmTaskDTO> buildTodoTaskPage(Pages<Task> pageResult,
                                                Map<String, ProcessInstance> processInstanceMap,
                                                Map<Long, SysUserDTO> userMap,
                                                Map<String, BpmProcessDefinitionInfoPO> processDefinitionInfoMap) {
        return BeanUtils.toBean(pageResult, BpmTaskDTO.class, taskVO -> {
            ProcessInstance processInstance = processInstanceMap.get(taskVO.getProcessInstanceId());
            if (processInstance == null) {
                return;
            }
            taskVO.setProcessInstance(BeanUtils.toBean(processInstance, BpmTaskDTO.ProcessInstance.class));
            SysUserDTO startUser = userMap.get(NumberUtils.parseLong(processInstance.getStartUserId()));
            taskVO.getProcessInstance().setStartUser(BeanUtils.toBean(startUser, UserSimpleBaseDTO.class));
            taskVO.getProcessInstance().setCreateTime(processInstance.getStartTime());

            List<KeyValue<String, String>> summary = FlowableUtils.getSummary(processDefinitionInfoMap.get(processInstance.getProcessDefinitionId()),
                    processInstance.getProcessVariables());
            // 摘要
            taskVO.getProcessInstance().setSummary(summary);

        });
    }

    default Pages<BpmTaskDTO> buildTaskPage(Pages<HistoricTaskInstance> pageResult,
                                                    Map<String, HistoricProcessInstance> processInstanceMap,
                                                    Map<Long, SysUserDTO> userMap,
                                                    Map<Long, SysDeptDTO> deptMap,
                                                    Map<String, BpmProcessDefinitionInfoPO> processDefinitionInfoMap) {
        List<BpmTaskDTO> taskVOList = CollectionUtils.convertList(pageResult.getPages(), task -> {
            BpmTaskDTO taskVO = BeanUtils.toBean(task, BpmTaskDTO.class);
            taskVO.setStatus(FlowableUtils.getTaskStatus(task)).setReason(FlowableUtils.getTaskReason(task));
            // 用户信息
            SysUserDTO assignUser = userMap.get(NumberUtils.parseLong(task.getAssignee()));
            if (assignUser != null) {
                taskVO.setAssigneeUser(BeanUtils.toBean(assignUser, UserSimpleBaseDTO.class));
                findAndThen(deptMap, assignUser.getDeptId(), dept -> taskVO.getAssigneeUser().setDeptName(dept.getDeptName()));
            }
            // 流程实例
            HistoricProcessInstance processInstance = processInstanceMap.get(taskVO.getProcessInstanceId());
            if (processInstance != null) {
                SysUserDTO startUser = userMap.get(NumberUtils.parseLong(processInstance.getStartUserId()));
                taskVO.setProcessInstance(BeanUtils.toBean(processInstance, BpmTaskDTO.ProcessInstance.class));
                taskVO.getProcessInstance().setStartUser(BeanUtils.toBean(startUser, UserSimpleBaseDTO.class));
                // 摘要
                taskVO.getProcessInstance().setSummary(FlowableUtils.getSummary(processDefinitionInfoMap.get(processInstance.getProcessDefinitionId()),
                        processInstance.getProcessVariables()));
            }
            return taskVO;
        });
        return PageConverterUtils.convert(taskVOList,pageResult.getPageNum(),pageResult.getPageSize(),pageResult.getTotalNum());
    }

    default List<BpmTaskDTO> buildTaskListByProcessInstanceId(List<HistoricTaskInstance> taskList,
                                                                 Map<Long, BpmFormPO> formMap,
                                                                 Map<Long, SysUserDTO> userMap,
                                                                 Map<Long, SysDeptDTO> deptMap) {
        return CollectionUtils.convertList(taskList, task -> {
            // 特殊：已取消的任务，不返回
            BpmTaskDTO taskVO = BeanUtils.toBean(task, BpmTaskDTO.class);
            Integer taskStatus = FlowableUtils.getTaskStatus(task);
            if (BpmTaskStatusEnum.isCancelStatus(taskStatus)) {
                return null;
            }
            taskVO.setStatus(taskStatus).setReason(FlowableUtils.getTaskReason(task));
            // 表单信息
            BpmFormPO form = MapUtil.get(formMap, NumberUtils.parseLong(task.getFormKey()), BpmFormPO.class);
            if (form != null) {
                taskVO.setFormId(form.getId()).setFormName(form.getName()).setFormConf(form.getConf())
                        .setFormFields(form.getFields()).setFormVariables(FlowableUtils.getTaskFormVariable(task));
            }
            // 用户信息
            buildTaskAssignee(taskVO, task.getAssignee(), userMap, deptMap);
            buildTaskOwner(taskVO, task.getOwner(), userMap, deptMap);
            return taskVO;
        });
    }

    default List<BpmTaskDTO> buildTaskListByParentTaskId(List<Task> taskList,
                                                            Map<Long, SysUserDTO> userMap,
                                                            Map<Long, SysDeptDTO> deptMap) {
        return convertList(taskList, task -> BeanUtils.toBean(task, BpmTaskDTO.class, taskVO -> {
            SysUserDTO assignUser = userMap.get(NumberUtils.parseLong(task.getAssignee()));
            if (assignUser != null) {
                taskVO.setAssigneeUser(BeanUtils.toBean(assignUser, UserSimpleBaseDTO.class));
                SysDeptDTO dept = deptMap.get(assignUser.getDeptId());
                if (dept != null) {
                    taskVO.getAssigneeUser().setDeptName(dept.getDeptName());
                }
            }
            SysUserDTO ownerUser = userMap.get(NumberUtils.parseLong(task.getOwner()));
            if (ownerUser != null) {
                taskVO.setOwnerUser(BeanUtils.toBean(ownerUser, UserSimpleBaseDTO.class));
                findAndThen(deptMap, ownerUser.getDeptId(), dept -> taskVO.getOwnerUser().setDeptName(dept.getDeptName()));
            }
        }));
    }

    default BpmTaskDTO buildTodoTask(Task todoTask, List<Task> childrenTasks,
                                        Map<Integer, BpmTaskDTO.OperationButtonSetting> buttonsSetting,
                                        BpmFormPO form) {
        BpmTaskDTO bpmTaskRespVO = BeanUtils.toBean(todoTask, BpmTaskDTO.class)
                .setStatus(FlowableUtils.getTaskStatus(todoTask)).setReason(FlowableUtils.getTaskReason(todoTask))
                .setButtonsSetting(buttonsSetting)
                .setChildren(convertList(childrenTasks, childTask -> BeanUtils.toBean(childTask, BpmTaskDTO.class)
                        .setStatus(FlowableUtils.getTaskStatus(childTask))));
        if (form != null) {
            bpmTaskRespVO.setFormId(form.getId()).setFormName(form.getName())
                    .setFormConf(form.getConf()).setFormFields(form.getFields());
        }
        return bpmTaskRespVO;
    }

//    default BpmMessageSendWhenTaskCreatedReqDTO convert(ProcessInstance processInstance, SysUserDTO startUser,
//                                                        Task task) {
//        BpmMessageSendWhenTaskCreatedReqDTO reqDTO = new BpmMessageSendWhenTaskCreatedReqDTO();
//        reqDTO.setProcessInstanceId(processInstance.getProcessInstanceId())
//                .setProcessInstanceName(processInstance.getName()).setStartUserId(startUser.getId())
//                .setStartUserNickname(startUser.getNickname()).setTaskId(task.getId()).setTaskName(task.getName())
//                .setAssigneeUserId(NumberUtils.parseLong(task.getAssignee()));
//        return reqDTO;
//    }

    default void buildTaskOwner(BpmTaskDTO task, String taskOwner,
                                Map<Long, SysUserDTO> userMap,
                                Map<Long, SysDeptDTO> deptMap) {
        SysUserDTO ownerUser = userMap.get(NumberUtils.parseLong(taskOwner));
        if (ownerUser != null) {
            task.setOwnerUser(BeanUtils.toBean(ownerUser, UserSimpleBaseDTO.class));
            findAndThen(deptMap, ownerUser.getDeptId(), dept -> task.getOwnerUser().setDeptName(dept.getDeptName()));
        }
    }

    default void buildTaskChildren(BpmTaskDTO task, Map<String, List<Task>> childrenTaskMap,
                                   Map<Long, SysUserDTO> userMap, Map<Long, SysDeptDTO> deptMap) {
        List<Task> childTasks = childrenTaskMap.get(task.getId());
        if (CollUtil.isNotEmpty(childTasks)) {
            task.setChildren(
                    convertList(childTasks, childTask -> {
                        BpmTaskDTO childTaskVO = BeanUtils.toBean(childTask, BpmTaskDTO.class);
                        childTaskVO.setStatus(FlowableUtils.getTaskStatus(childTask));
                        buildTaskOwner(childTaskVO, childTask.getOwner(), userMap, deptMap);
                        buildTaskAssignee(childTaskVO, childTask.getAssignee(), userMap, deptMap);
                        return childTaskVO;
                    })
            );
        }
    }

    default void buildTaskAssignee(BpmTaskDTO task, String taskAssignee,
                                   Map<Long, SysUserDTO> userMap,
                                   Map<Long, SysDeptDTO> deptMap) {
        SysUserDTO assignUser = userMap.get(NumberUtils.parseLong(taskAssignee));
        if (assignUser != null) {
            task.setAssigneeUser(BeanUtils.toBean(assignUser, UserSimpleBaseDTO.class));
            findAndThen(deptMap, assignUser.getDeptId(), dept -> task.getAssigneeUser().setDeptName(dept.getDeptName()));
        }
    }

    /**
     * 将父任务的属性，拷贝到子任务（加签任务）
     * <p>
     * 为什么不使用 mapstruct 映射？因为 TaskEntityImpl 还有很多其他属性，这里我们只设置我们需要的。
     * 使用 mapstruct 会将里面嵌套的各个属性值都设置进去，会出现意想不到的问题。
     *
     * @param parentTask 父任务
     * @param childTask  加签任务
     */
    default void copyTo(TaskEntityImpl parentTask, TaskEntityImpl childTask) {
        childTask.setName(parentTask.getName());
        childTask.setDescription(parentTask.getDescription());
        childTask.setCategory(parentTask.getCategory());
        childTask.setParentTaskId(parentTask.getId());
        childTask.setProcessDefinitionId(parentTask.getProcessDefinitionId());
        childTask.setProcessInstanceId(parentTask.getProcessInstanceId());
        childTask.setTaskDefinitionKey(parentTask.getTaskDefinitionKey());
        childTask.setTaskDefinitionId(parentTask.getTaskDefinitionId());
        childTask.setPriority(parentTask.getPriority());
        childTask.setCreateTime(new Date());
        childTask.setTenantId(parentTask.getTenantId());
    }

}
