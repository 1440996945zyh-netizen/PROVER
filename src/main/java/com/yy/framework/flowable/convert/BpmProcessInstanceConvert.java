package com.yy.framework.flowable.convert;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.yy.common.flowable.constants.BpmnVariableConstants;
import com.yy.common.flowable.enums.BpmTaskStatusEnum;
import com.yy.common.flowable.utils.*;
import com.yy.common.page.Pages;
import com.yy.framework.flowable.event.BpmProcessInstanceStatusEvent;
import com.yy.ppm.flowable.bean.dto.*;
import com.yy.ppm.flowable.bean.po.BpmCategoryPO;
import com.yy.ppm.flowable.bean.po.BpmProcessDefinitionInfoPO;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import java.util.*;

import static com.yy.common.flowable.utils.CollectionUtils.convertList;
import static com.yy.common.flowable.utils.CollectionUtils.convertSet;

/**
 * 流程实例 Convert
 *
 */
@Mapper
public interface BpmProcessInstanceConvert {

    BpmProcessInstanceConvert INSTANCE = Mappers.getMapper(BpmProcessInstanceConvert.class);

    default Pages<BpmProcessInstanceDTO> buildProcessInstancePage(Pages<HistoricProcessInstance> pageResult,
                                                                  Map<String, ProcessDefinition> processDefinitionMap,
                                                                  Map<String, BpmCategoryPO> categoryMap,
                                                                  Map<String, List<Task>> taskMap,
                                                                  Map<Long, SysUserDTO> userMap,
                                                                  Map<Long, SysDeptDTO> deptMap,
                                                                  Map<String, BpmProcessDefinitionInfoPO> processDefinitionInfoMap) {
        Pages<BpmProcessInstanceDTO> vpPageResult = BeanUtils.toBean(pageResult, BpmProcessInstanceDTO.class);
        for (int i = 0; i < pageResult.getPages().size(); i++) {
            BpmProcessInstanceDTO respVO = vpPageResult.getPages().get(i);
            respVO.setStatus(FlowableUtils.getProcessInstanceStatus(pageResult.getPages().get(i)));
            MapUtils.findAndThen(processDefinitionMap, respVO.getProcessDefinitionId(),
                    processDefinition -> respVO.setCategory(processDefinition.getCategory())
                            .setProcessDefinition(BeanUtils.toBean(processDefinition, BpmProcessDefinitionDTO.class)));
            MapUtils.findAndThen(categoryMap, respVO.getCategory(), category -> respVO.setCategoryName(category.getName()));
            respVO.setTasks(BeanUtils.toBean(taskMap.get(respVO.getId()), BpmProcessInstanceDTO.Task.class));
            // user
            if (userMap != null) {
                SysUserDTO startUser = userMap.get(NumberUtils.parseLong(pageResult.getPages().get(i).getStartUserId()));
                if (startUser != null) {
                    respVO.setStartUser(BeanUtils.toBean(startUser, UserSimpleBaseDTO.class));
                    MapUtils.findAndThen(deptMap, startUser.getDeptId(), dept -> respVO.getStartUser().setDeptName(dept.getDeptName()));
                }
                if (CollUtil.isNotEmpty(respVO.getTasks())) {
                    respVO.getTasks().forEach(task -> {
                        SysUserDTO assigneeUser = userMap.get(task.getAssignee());
                        if (assigneeUser!= null) {
                            task.setAssigneeUser(BeanUtils.toBean(assigneeUser, UserSimpleBaseDTO.class));
                            MapUtils.findAndThen(deptMap, assigneeUser.getDeptId(), dept -> task.getAssigneeUser().setDeptName(dept.getDeptName()));
                        }
                    });
                }
            }
            // 摘要
            respVO.setSummary(FlowableUtils.getSummary(processDefinitionInfoMap.get(respVO.getProcessDefinitionId()),
                    pageResult.getPages().get(i).getProcessVariables()));
            // 表单
            respVO.setFormVariables(pageResult.getPages().get(i).getProcessVariables());
        }
        return vpPageResult;
    }

    default BpmProcessInstanceDTO buildProcessInstance(HistoricProcessInstance processInstance,
                                                          ProcessDefinition processDefinition,
                                                          BpmProcessDefinitionInfoPO processDefinitionInfo,
                                                          SysUserDTO startUser,
                                                          SysDeptDTO dept) {
        BpmProcessInstanceDTO respVO = BeanUtils.toBean(processInstance, BpmProcessInstanceDTO.class);
        respVO.setStatus(FlowableUtils.getProcessInstanceStatus(processInstance))
                .setFormVariables(FlowableUtils.getProcessInstanceFormVariable(processInstance));
        // definition
        respVO.setProcessDefinition(BeanUtils.toBean(processDefinition, BpmProcessDefinitionDTO.class));
        copyTo(processDefinitionInfo, respVO.getProcessDefinition());
        // user
        if (startUser != null) {
            respVO.setStartUser(BeanUtils.toBean(startUser, UserSimpleBaseDTO.class));
            if (dept != null) {
                respVO.getStartUser().setDeptName(dept.getDeptName());
            }
        }
        return respVO;
    }

    @Mapping(source = "from.id", target = "to.id", ignore = true)
    void copyTo(BpmProcessDefinitionInfoPO from, @MappingTarget BpmProcessDefinitionDTO to);

    default BpmProcessInstanceStatusEvent buildProcessInstanceStatusEvent(Object source, ProcessInstance instance,
                                                                          Integer status, String reason) {
        return new BpmProcessInstanceStatusEvent(source).setId(instance.getId()).setStatus(status).setReason(reason)
                .setProcessDefinitionKey(instance.getProcessDefinitionKey()).setBusinessKey(instance.getBusinessKey());
    }

//    default BpmMessageSendWhenProcessInstanceApproveReqDTO buildProcessInstanceApproveMessage(ProcessInstance instance) {
//        return new BpmMessageSendWhenProcessInstanceApproveReqDTO()
//                .setStartUserId(NumberUtils.parseLong(instance.getStartUserId()))
//                .setProcessInstanceId(instance.getId())
//                .setProcessInstanceName(instance.getName());
//    }

//    default BpmMessageSendWhenProcessInstanceRejectReqDTO buildProcessInstanceRejectMessage(ProcessInstance instance, String reason) {
//        return new BpmMessageSendWhenProcessInstanceRejectReqDTO()
//            .setProcessInstanceName(instance.getName())
//            .setProcessInstanceId(instance.getId())
//            .setReason(reason)
//            .setStartUserId(NumberUtils.parseLong(instance.getStartUserId()));
//    }

    default BpmProcessInstanceBpmnModelViewDTO buildProcessInstanceBpmnModelView(HistoricProcessInstance processInstance,
                                                                                    List<HistoricTaskInstance> taskInstances,
                                                                                    BpmnModel bpmnModel,
                                                                                    Set<String> unfinishedTaskActivityIds,
                                                                                    Set<String> finishedTaskActivityIds,
                                                                                    Set<String> finishedSequenceFlowActivityIds,
                                                                                    Set<String> rejectTaskActivityIds,
                                                                                    Map<Long, SysUserDTO> userMap,
                                                                                    Map<Long, SysDeptDTO> deptMap) {
        BpmProcessInstanceBpmnModelViewDTO respVO = new BpmProcessInstanceBpmnModelViewDTO();
        // 基本信息
        respVO.setProcessInstance(BeanUtils.toBean(processInstance, BpmProcessInstanceDTO.class, o -> o
                        .setStatus(FlowableUtils.getProcessInstanceStatus(processInstance)))
                        .setStartUser(buildUser(processInstance.getStartUserId(), userMap, deptMap)));
        respVO.setTasks(convertList(taskInstances, task -> BeanUtils.toBean(task, BpmTaskDTO.class)
                .setStatus(FlowableUtils.getTaskStatus(task)).setReason(FlowableUtils.getTaskReason(task))
                .setAssigneeUser(buildUser(task.getAssignee(), userMap, deptMap))
                .setOwnerUser(buildUser(task.getOwner(), userMap, deptMap))));
        respVO.setBpmnXml(BpmnModelUtils.getBpmnXml(bpmnModel));
//        respVO.setSimpleModel(simpleModel);
        // 进度信息
        respVO.setUnfinishedTaskActivityIds(unfinishedTaskActivityIds)
                .setFinishedTaskActivityIds(finishedTaskActivityIds)
                .setFinishedSequenceFlowActivityIds(finishedSequenceFlowActivityIds)
                .setRejectedTaskActivityIds(rejectTaskActivityIds);
        return respVO;
    }

    default UserSimpleBaseDTO buildUser(String userIdStr,
                                       Map<Long, SysUserDTO> userMap,
                                       Map<Long, SysDeptDTO> deptMap) {
        if (StrUtil.isEmpty(userIdStr)) {
            return null;
        }
        Long userId = NumberUtils.parseLong(userIdStr);
        return buildUser(userId, userMap, deptMap);
    }

    default UserSimpleBaseDTO buildUser(Long userId,
                                                    Map<Long, SysUserDTO> userMap,
                                                    Map<Long, SysDeptDTO> deptMap) {
        if (userId == null) {
            return null;
        }
        SysUserDTO user = userMap.get(userId);
        if (user == null) {
            return null;
        }
        UserSimpleBaseDTO userVO = BeanUtils.toBean(user, UserSimpleBaseDTO.class);
        SysDeptDTO dept = user.getDeptId() != null ? deptMap.get(user.getDeptId()) : null;
        if (dept != null) {
            userVO.setDeptName(dept.getDeptName());
        }
        return userVO;
    }

    default BpmApprovalDetailDTO.ActivityNodeTask buildApprovalTaskInfo(HistoricTaskInstance task) {
        if (task == null) {
            return null;
        }
        return BeanUtils.toBean(task, BpmApprovalDetailDTO.ActivityNodeTask.class)
                .setStatus(FlowableUtils.getTaskStatus(task)).setReason(FlowableUtils.getTaskReason(task))
                .setSignPicUrl(FlowableUtils.getTaskSignPicUrl(task));
    }

    default Set<Long> parseUserIds(HistoricProcessInstance processInstance,
                                   List<BpmApprovalDetailDTO.ActivityNode> activityNodes,
                                   BpmTaskDTO todoTask) {
        Set<Long> userIds = new HashSet<>();
        if (processInstance != null) {
            userIds.add(NumberUtils.parseLong(processInstance.getStartUserId()));
        }
        for (BpmApprovalDetailDTO.ActivityNode activityNode : activityNodes) {
            CollUtil.addAll(userIds, convertSet(activityNode.getTasks(), BpmApprovalDetailDTO.ActivityNodeTask::getAssignee));
            CollUtil.addAll(userIds, convertSet(activityNode.getTasks(), BpmApprovalDetailDTO.ActivityNodeTask::getOwner));
            CollUtil.addAll(userIds, activityNode.getCandidateUserIds());
        }
        if (todoTask != null) {
            CollUtil.addIfAbsent(userIds, todoTask.getAssignee());
            CollUtil.addIfAbsent(userIds, todoTask.getOwner());
            if (CollUtil.isNotEmpty(todoTask.getChildren())) {
                CollUtil.addAll(userIds, convertSet(todoTask.getChildren(), BpmTaskDTO::getAssignee));
                CollUtil.addAll(userIds, convertSet(todoTask.getChildren(), BpmTaskDTO::getOwner));
            }
        }
        return userIds;
    }

    default Set<Long> parseUserIds02(HistoricProcessInstance processInstance,
                                     List<HistoricTaskInstance> tasks) {
        Set<Long> userIds = SetUtils.asSet(Long.valueOf(processInstance.getStartUserId()));
        tasks.forEach(task -> {
            CollUtil.addIfAbsent(userIds, NumberUtils.parseLong((task.getAssignee())));
            CollUtil.addIfAbsent(userIds, NumberUtils.parseLong((task.getOwner())));
        });
        return userIds;
    }

    default BpmApprovalDetailDTO buildApprovalDetail(BpmnModel bpmnModel,
                                                        ProcessDefinition processDefinition,
                                                        BpmProcessDefinitionInfoPO processDefinitionInfo,
                                                        HistoricProcessInstance processInstance,
                                                        Integer processInstanceStatus,
                                                        List<BpmApprovalDetailDTO.ActivityNode> activityNodes,
                                                        BpmTaskDTO todoTask,
                                                        Map<String, String> formFieldsPermission,
                                                        Map<Long, SysUserDTO> userMap,
                                                        Map<Long, SysDeptDTO> deptMap) {
        // 1.1 流程实例
        BpmProcessInstanceDTO processInstanceResp = null;
        if (processInstance != null) {
            SysUserDTO startUser = userMap.get(NumberUtils.parseLong(processInstance.getStartUserId()));
            SysDeptDTO dept = startUser != null ? deptMap.get(startUser.getDeptId()) : null;
            processInstanceResp = buildProcessInstance(processInstance, null, null, startUser, dept);
        }

        // 1.2 流程定义
        BpmProcessDefinitionDTO definitionResp = BpmProcessDefinitionConvert.INSTANCE.buildProcessDefinition(
                processDefinition, null, processDefinitionInfo, null, null, bpmnModel);

        // 1.3 流程节点
        activityNodes.forEach(approveNode -> {
            if (approveNode.getTasks() != null) {
                approveNode.getTasks().forEach(task -> {
                    task.setAssigneeUser(buildUser(task.getAssignee(), userMap, deptMap));
                    task.setOwnerUser(buildUser(task.getOwner(), userMap, deptMap));
                });
            }
            approveNode.setCandidateUsers(convertList(approveNode.getCandidateUserIds(), userId -> buildUser(userId, userMap, deptMap)));
        });

        // 1.4 待办任务
        if (todoTask != null) {
            todoTask.setAssigneeUser(buildUser(todoTask.getAssignee(), userMap, deptMap));
            todoTask.setOwnerUser(buildUser(todoTask.getOwner(), userMap, deptMap));
            if (CollUtil.isNotEmpty(todoTask.getChildren())) {
                todoTask.getChildren().forEach(childTask -> {
                    childTask.setAssigneeUser(buildUser(childTask.getAssignee(), userMap, deptMap));
                    childTask.setOwnerUser(buildUser(childTask.getOwner(), userMap, deptMap));
                });
            }
        }

        // 2. 拼接起来
        return new BpmApprovalDetailDTO().setStatus(processInstanceStatus)
                .setProcessDefinition(definitionResp)
                .setProcessInstance(processInstanceResp)
                .setFormFieldsPermission(formFieldsPermission)
                .setTodoTask(todoTask)
                .setActivityNodes(activityNodes);
    }

    default BpmProcessPrintDataDTO buildProcessInstancePrintData(HistoricProcessInstance historicProcessInstance,
                                                                    BpmProcessDefinitionInfoPO processDefinitionInfo,
                                                                    List<HistoricTaskInstance> tasks,
                                                                    Map<Long, SysUserDTO> userMap,
                                                                    UserSimpleBaseDTO startUser) {
        BpmModelMetaInfoDTO.PrintTemplateSetting printTemplateSetting = processDefinitionInfo.getPrintTemplateSetting();
        BpmProcessPrintDataDTO printData = new BpmProcessPrintDataDTO();
        // 打印模板是否开启
        printData.setPrintTemplateEnable(printTemplateSetting != null && Boolean.TRUE.equals(printTemplateSetting.getEnable()));
        // 流程相关数据
        BpmProcessInstanceDTO processInstance = new BpmProcessInstanceDTO()
                .setId(historicProcessInstance.getId()).setName(historicProcessInstance.getName())
                .setBusinessKey(historicProcessInstance.getBusinessKey())
                .setStartTime(DateUtils.of(historicProcessInstance.getStartTime()))
                .setEndTime(DateUtils.of(historicProcessInstance.getEndTime()))
                .setStartUser(startUser).setStatus(FlowableUtils.getProcessInstanceStatus(historicProcessInstance))
                .setFormVariables(historicProcessInstance.getProcessVariables())
                .setProcessDefinition(BeanUtils.toBean(processDefinitionInfo, BpmProcessDefinitionDTO.class));
        printData.setProcessInstance(processInstance);
        // 审批历史
        List<BpmProcessPrintDataDTO.Task> approveTasks = new ArrayList<>(tasks.size());
        tasks.forEach(item -> {
            Map<String, Object> taskLocalVariables = item.getTaskLocalVariables();
            BpmProcessPrintDataDTO.Task approveTask = new BpmProcessPrintDataDTO.Task();
            approveTask.setName(item.getName());
            approveTask.setId(item.getId());
            approveTask.setSignPicUrl((String) taskLocalVariables.get(BpmnVariableConstants.TASK_SIGN_PIC_URL));
            approveTask.setDescription(StrUtil.format("{} / {} / {} / {} / {}",
                    userMap.get(Long.valueOf(item.getAssignee())).getUserName(),
                    item.getName(),
                    DateUtil.formatDateTime(item.getEndTime()),
                    BpmTaskStatusEnum.valueOf((Integer) taskLocalVariables.get(BpmnVariableConstants.TASK_VARIABLE_STATUS)).getName(),
                    taskLocalVariables.get(BpmnVariableConstants.TASK_VARIABLE_REASON)));
            approveTasks.add(approveTask);
        });
        printData.setTasks(approveTasks);
        // 自定义模板
        if (printData.getPrintTemplateEnable() && printTemplateSetting != null) {
            printData.setPrintTemplateHtml(printTemplateSetting.getTemplate());
        }
        return printData;
    }

}
