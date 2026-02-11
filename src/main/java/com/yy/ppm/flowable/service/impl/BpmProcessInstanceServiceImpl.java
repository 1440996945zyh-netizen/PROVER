package com.yy.ppm.flowable.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.*;
import com.yy.common.flowable.constants.BpmnModelConstants;
import com.yy.common.flowable.constants.BpmnVariableConstants;
import com.yy.common.flowable.constants.ErrorCodeConstants;
import com.yy.common.flowable.enums.*;
import com.yy.common.flowable.utils.*;
import com.yy.common.page.Pages;
import com.yy.common.util.PageConverterUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.flowable.convert.BpmProcessInstanceConvert;
import com.yy.framework.flowable.event.BpmProcessInstanceEventPublisher;
import com.yy.framework.flowable.redis.BpmProcessIdRedisDAO;
import com.yy.framework.flowable.strategy.BpmTaskCandidateInvoker;
import com.yy.ppm.flowable.bean.dto.*;
import com.yy.ppm.flowable.bean.po.BpmProcessDefinitionInfoPO;
import com.yy.ppm.flowable.mapper.BpmBusinessInstanceMapper;
import com.yy.ppm.flowable.service.BpmProcessDefinitionService;
import com.yy.ppm.flowable.service.BpmProcessInstanceService;
import com.yy.ppm.flowable.service.BpmTaskService;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import com.yy.ppm.system.service.SysDeptService;
import com.yy.ppm.system.service.SysUserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.*;
import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceBuilder;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.validation.annotation.Validated;
import java.util.*;
import java.util.stream.Collectors;

import static com.yy.common.flowable.constants.BpmnModelConstants.START_USER_NODE_ID;
import static com.yy.common.flowable.constants.ErrorCodeConstants.*;
import static com.yy.common.flowable.utils.BpmnModelUtils.parseNodeType;
import static com.yy.common.flowable.utils.CollectionUtils.*;
import static com.yy.common.flowable.utils.ServiceExceptionUtil.exception;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.flowable.bpmn.constants.BpmnXMLConstants.*;

/**
 * 流程实例 Service 实现类
 * <p>
 * ProcessDefinition & ProcessInstance & Execution & Task 的关系：
 * 1. <a href="https://blog.csdn.net/bobozai86/article/details/105210414" />
 * <p>
 * HistoricProcessInstance & ProcessInstance 的关系：
 * 1. <a href=" https://my.oschina.net/843294669/blog/71902" />
 * <p>
 * 简单来说，前者 = 历史 + 运行中的流程实例，后者仅是运行中的流程实例
 *
 */
@Service
@Validated
@Slf4j
public class BpmProcessInstanceServiceImpl implements BpmProcessInstanceService {

    @Resource
    private RuntimeService runtimeService;
    @Resource
    private HistoryService historyService;

    @Resource
    private BpmProcessDefinitionService processDefinitionService;
    @Resource
    @Lazy // 避免循环依赖
    private BpmTaskService taskService;

    @Resource
    private BpmProcessInstanceEventPublisher processInstanceEventPublisher;

    @Resource
    private BpmTaskCandidateInvoker taskCandidateInvoker;

    @Resource
    private BpmProcessIdRedisDAO processIdRedisDAO;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysDeptService sysDeptService;

    @Autowired
    private IdentityService identityService;

    @Resource
    private BpmBusinessInstanceMapper bpmBusinessInstanceMapper;


    @Autowired
    private Snowflake snowflake;

    // ========== Query 查询相关方法 ==========

    @Override
    public ProcessInstance getProcessInstance(String id) {
        return runtimeService.createProcessInstanceQuery()
                .includeProcessVariables()
                .processInstanceId(id)
                .singleResult();
    }

    @Override
    public List<ProcessInstance> getProcessInstances(Set<String> ids) {
        return runtimeService.createProcessInstanceQuery().processInstanceIds(ids).includeProcessVariables().list();
    }

    @Override
    public HistoricProcessInstance getHistoricProcessInstance(String id) {
        return historyService.createHistoricProcessInstanceQuery().processInstanceId(id).includeProcessVariables()
                .singleResult();
    }

    @Override
    public List<HistoricProcessInstance> getHistoricProcessInstances(Set<String> ids) {
        return historyService.createHistoricProcessInstanceQuery().processInstanceIds(ids).includeProcessVariables()
                .list();
    }

    private Map<String, String> getFormFieldsPermission(BpmnModel bpmnModel,
                                                        String activityId, String taskId) {
        // 1. 获取流程活动编号。流程活动 Id 为空事，从流程任务中获取流程活动 Id
        if (StrUtil.isEmpty(activityId) && StrUtil.isNotEmpty(taskId)) {
            activityId = Optional.ofNullable(taskService.getHistoricTask(taskId))
                    .map(HistoricTaskInstance::getTaskDefinitionKey).orElse(null);
        }
        if (StrUtil.isEmpty(activityId)) {
            return null;
        }

        // 2. 从 BpmnModel 中解析表单字段权限
        return BpmnModelUtils.parseFormFieldsPermission(bpmnModel, activityId);
    }

    /**
     * 获取审批详情。
     * <p>
     * 可以是准备发起的流程、进行中的流程、已经结束的流程
     *
     * @param loginUserId  登录人的用户编号
     * @param reqVO 请求信息
     * @return 流程实例的进度
     * 审批前（预测） 通过 getSimulateApproveNodeList  预判流程如果发起，将会经过哪些节点、由谁审批
     * 审批中（追踪） 已完成的节点、进行中的节点、未来待进行的节点
     * 审批后（回溯） 要展示历史审批记录
     */
    @Override
    public BpmApprovalDetailDTO getApprovalDetail(Long loginUserId, BpmApprovalDetailSearchDTO reqVO) {
        // 1.1 从 reqVO 中，读取公共变量
        Long startUserId = loginUserId; // 流程发起人
        HistoricProcessInstance historicProcessInstance = null; // 流程实例
        Integer processInstanceStatus = BpmProcessInstanceStatusEnum.NOT_START.getStatus(); // 流程状态
        Map<String, Object> processVariables = new HashMap<>(); // 流程变量
        // 1.2 如果是流程已发起的场景，则使用流程实例的数据
        if (reqVO.getProcessInstanceId() != null) {
            historicProcessInstance = getHistoricProcessInstance(reqVO.getProcessInstanceId());
            if (historicProcessInstance == null) {
                throw exception(ErrorCodeConstants.PROCESS_INSTANCE_NOT_EXISTS);
            }
            startUserId = Long.valueOf(historicProcessInstance.getStartUserId());
            processInstanceStatus = FlowableUtils.getProcessInstanceStatus(historicProcessInstance);
            // 合并 DB 和前端传递的流量变量，以前端的为主
            if (CollUtil.isNotEmpty(historicProcessInstance.getProcessVariables())) {
                processVariables.putAll(historicProcessInstance.getProcessVariables());
            }
        }
        if (CollUtil.isNotEmpty(reqVO.getProcessVariables())) {
            processVariables.putAll(reqVO.getProcessVariables());
        }
        // 特殊：如果是未发起的场景，则设置发起用户，解决“发起流程”时，需要使用到该变量的问题。例如说：https://t.zsxq.com/fMw5g
        if (historicProcessInstance == null) {
            processVariables.put(BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_START_USER_ID, loginUserId);
        }
        // 1.3 读取其它相关数据
        ProcessDefinition processDefinition = processDefinitionService.getProcessDefinition(
                historicProcessInstance != null ? historicProcessInstance.getProcessDefinitionId()
                        : reqVO.getProcessDefinitionId());
        BpmProcessDefinitionInfoPO processDefinitionInfo = processDefinitionService
                .getProcessDefinitionInfo(processDefinition.getId());
        BpmnModel bpmnModel = processDefinitionService.getProcessDefinitionBpmnModel(processDefinition.getId());

        // 2.1 已结束 + 进行中的活动节点
        List<BpmApprovalDetailDTO.ActivityNode> endActivityNodes = null; // 已结束的审批信息
        List<BpmApprovalDetailDTO.ActivityNode> runActivityNodes = null; // 进行中的审批信息
        List<HistoricActivityInstance> activities = null; // 流程实例列表
        if (reqVO.getProcessInstanceId() != null) {
            activities = taskService.getActivityListByProcessInstanceId(reqVO.getProcessInstanceId());
            List<HistoricTaskInstance> tasks = taskService.getTaskListByProcessInstanceId(reqVO.getProcessInstanceId(),
                    true);
            endActivityNodes = getEndActivityNodeList(startUserId, bpmnModel, processDefinitionInfo,
                    historicProcessInstance, processInstanceStatus, activities, tasks);
            runActivityNodes = getRunApproveNodeList(startUserId, bpmnModel, processDefinition, processVariables,
                    activities, tasks);
        }

        // 2.2 流程已经结束，直接 return，无需预测
        if (BpmProcessInstanceStatusEnum.isProcessEndStatus(processInstanceStatus)) {
            return buildApprovalDetail(reqVO, bpmnModel, processDefinition, processDefinitionInfo,
                    historicProcessInstance,
                    processInstanceStatus, endActivityNodes, runActivityNodes, null, null);
        }

        // 3.1 计算当前登录用户的待办任务
        BpmTaskDTO todoTask = taskService.getTodoTask(loginUserId, reqVO.getTaskId(), reqVO.getProcessInstanceId());

        // 3.2 获取由于退回操作，需要预测的节点。从流程变量中获取，回退操作会设置这些变量
//        Set<String> needSimulateTaskDefKeysByReturn = new HashSet<>();
//        if (StrUtil.isNotEmpty(reqVO.getProcessInstanceId())) {
//            Object needSimulateTaskIds = runtimeService.getVariable(reqVO.getProcessInstanceId(), BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_NEED_SIMULATE_TASK_IDS);
//            needSimulateTaskDefKeysByReturn.addAll(Convert.toSet(String.class, needSimulateTaskIds));
//        }

        // 3.2 从 processVariables 中获取，如果 Map 里没有，再尝试从历史服务获取
        Set<String> needSimulateTaskDefKeysByReturn = new HashSet<>();
        Object needSimulateTaskIds = processVariables.get(BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_NEED_SIMULATE_TASK_IDS);

        if (needSimulateTaskIds == null && reqVO.getProcessInstanceId() != null) {
            // 兜底：如果 Map 里没有，尝试查历史变量表（HistoryService 既查运行中也查已结束，不会报错）
            HistoricVariableInstance varInstance = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(reqVO.getProcessInstanceId())
                    .variableName(BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_NEED_SIMULATE_TASK_IDS)
                    .singleResult();
            if (varInstance != null) {
                needSimulateTaskIds = varInstance.getValue();
            }
        }
        if (needSimulateTaskIds != null) {
            needSimulateTaskDefKeysByReturn.addAll(Convert.toSet(String.class, needSimulateTaskIds));
        }


        // 移除运行中的节点，运行中的节点无需预测
        if (CollUtil.isNotEmpty(runActivityNodes)) {
            runActivityNodes.forEach( activityNode -> needSimulateTaskDefKeysByReturn.remove(activityNode.getId()));
        }

        // 3.3 预测未运行节点的审批信息
        List<BpmApprovalDetailDTO.ActivityNode> simulateActivityNodes = getSimulateApproveNodeList(startUserId, bpmnModel,
                processDefinitionInfo,
                processVariables, activities, needSimulateTaskDefKeysByReturn);

        // 4. 拼接最终数据
        return buildApprovalDetail(reqVO, bpmnModel, processDefinition, processDefinitionInfo, historicProcessInstance,
                processInstanceStatus, endActivityNodes, runActivityNodes, simulateActivityNodes, todoTask);
    }

    @Override
    public List<BpmApprovalDetailDTO.ActivityNode> getNextApprovalNodes(Long loginUserId, BpmApprovalDetailSearchDTO reqVO) {
        // 1.1 校验任务存在，且是当前用户的
        Task task = taskService.validateTask(loginUserId, reqVO.getTaskId());
        // 1.2 校验流程实例存在
        ProcessInstance instance = getProcessInstance(task.getProcessInstanceId());
        if (instance == null) {
            throw exception(PROCESS_INSTANCE_NOT_EXISTS);
        }
        HistoricProcessInstance historicProcessInstance = getHistoricProcessInstance(task.getProcessInstanceId());
        if (historicProcessInstance == null) {
            throw exception(ErrorCodeConstants.PROCESS_INSTANCE_NOT_EXISTS);
        }
        // 1.3 校验BpmnModel
        BpmnModel bpmnModel = processDefinitionService.getProcessDefinitionBpmnModel(task.getProcessDefinitionId());
        if (bpmnModel == null) {
            return null;
        }

        // 2. 设置流程变量
        Map<String, Object> processVariables = new HashMap<>();
        // 2.1 获取历史中流程变量
        if (CollUtil.isNotEmpty(historicProcessInstance.getProcessVariables())) {
            processVariables.putAll(historicProcessInstance.getProcessVariables());
        }
        // 2.2 合并前端传递的流程变量，以前端为准
        if (CollUtil.isNotEmpty(reqVO.getProcessVariables())) {
            processVariables.putAll(reqVO.getProcessVariables());
        }

        // 3. 获取下一个将要执行的节点集合
        FlowElement flowElement = bpmnModel.getFlowElement(task.getTaskDefinitionKey());
        List<FlowNode> nextFlowNodes = BpmnModelUtils.getNextFlowNodes(flowElement, bpmnModel, processVariables);
        // 仅仅获取 UserTask 节点  TODO add from jason：如果网关节点和网关节点相连，获取下个 UserTask. 貌似有点不准。
        List<FlowNode> nextUserTaskList = CollectionUtils.filterList(nextFlowNodes, node -> node instanceof UserTask);
        List<BpmApprovalDetailDTO.ActivityNode> nextActivityNodes = convertList(nextUserTaskList, node -> new BpmApprovalDetailDTO.ActivityNode().setId(node.getId())
                .setName(node.getName()).setNodeType(BpmSimpleModelNodeTypeEnum.APPROVE_NODE.getType())
                .setStatus(BpmTaskStatusEnum.RUNNING.getStatus())
                .setCandidateStrategy(BpmnModelUtils.parseCandidateStrategy(node))
                .setCandidateUserIds(getTaskCandidateUserList(bpmnModel, node.getId(),
                        loginUserId, historicProcessInstance.getProcessDefinitionId(), processVariables)));
        if (CollUtil.isEmpty(nextActivityNodes)) {
            return nextActivityNodes;
        }

        // 4. 拼接基础信息
        Map<Long, SysUserDTO> userMap = sysUserService.getUserMap(
                convertSetByFlatMap(nextActivityNodes, BpmApprovalDetailDTO.ActivityNode::getCandidateUserIds, Collection::stream));
        Map<Long, SysDeptDTO> deptMap = sysDeptService.getDeptMap(convertSet(userMap.values(), SysUserDTO::getDeptId));
        nextActivityNodes.forEach(node -> node.setCandidateUsers(convertList(node.getCandidateUserIds(), userId -> {
            SysUserDTO user = userMap.get(userId);
            if (user != null) {
                return BpmProcessInstanceConvert.INSTANCE.buildUser(userId, userMap, deptMap);
            }
            return null;
        })));
        return nextActivityNodes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Pages<HistoricProcessInstance> getProcessInstancePage(Long userId,
                                                                 BpmProcessInstanceSearchDTO pageReqVO) {
        // 1. 构建查询条件
        HistoricProcessInstanceQuery processInstanceQuery = historyService.createHistoricProcessInstanceQuery()
                .includeProcessVariables()
//                .processInstanceTenantId(FlowableUtils.getTenantId())
                .orderByProcessInstanceStartTime().desc();

        if (userId != null) { // 【我的流程】菜单时，需要传递该字段
            processInstanceQuery.startedBy(String.valueOf(userId));
        } else if (pageReqVO.getStartUserId() != null) { // 【管理流程】菜单时，才会传递该字段
            processInstanceQuery.startedBy(String.valueOf(pageReqVO.getStartUserId()));
        }
        if (StrUtil.isNotEmpty(pageReqVO.getName())) {
            processInstanceQuery.processInstanceNameLike("%" + pageReqVO.getName() + "%");
        }
        if (StrUtil.isNotBlank(pageReqVO.getProcessDefinitionName())) {
            processInstanceQuery.processDefinitionNameLike("%" + pageReqVO.getProcessDefinitionName() + "%");
        }
        if (StrUtil.isNotEmpty(pageReqVO.getProcessDefinitionKey())) {
            processInstanceQuery.processDefinitionKey(pageReqVO.getProcessDefinitionKey());

        }
        if (StrUtil.isNotEmpty(pageReqVO.getCategory())) {
            processInstanceQuery.processDefinitionCategory(pageReqVO.getCategory());
        }
        if (pageReqVO.getStatus() != null) {
            processInstanceQuery.variableValueEquals(BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_STATUS,
                    pageReqVO.getStatus());
        }
        if (ArrayUtil.isNotEmpty(pageReqVO.getCreateTime())) {
            processInstanceQuery.startedAfter(DateUtils.of(pageReqVO.getCreateTime()[0]));
            processInstanceQuery.startedBefore(DateUtils.of(pageReqVO.getCreateTime()[1]));
        }
        if (ArrayUtil.isNotEmpty(pageReqVO.getEndTime())) {
            processInstanceQuery.finishedAfter(DateUtils.of(pageReqVO.getEndTime()[0]));
            processInstanceQuery.finishedBefore(DateUtils.of(pageReqVO.getEndTime()[1]));
        }
        // 表单字段查询
        Map<String, Object> formFieldsParams = JsonUtils.parseObject(pageReqVO.getFormFieldsParams(), Map.class);
        if (CollUtil.isNotEmpty(formFieldsParams)) {
            formFieldsParams.forEach((key, value) -> {
                if (StrUtil.isEmpty(String.valueOf(value))) {
                    return;
                }
                // TODO @lesan：应支持多种类型的查询方式，目前只有字符串全等
                processInstanceQuery.variableValueEquals(key, value);
            });
        }

        // 2.1 查询数量
        long processInstanceCount = processInstanceQuery.count();

        // 2.2 查询列表
        int startData = (pageReqVO.getStartPage() - 1) * pageReqVO.getPageSize();
        List<HistoricProcessInstance> processInstanceList = processInstanceQuery.listPage(startData,
                pageReqVO.getPageSize());
        return PageConverterUtils.convert(processInstanceList,pageReqVO.getStartPage(),pageReqVO.getPageSize(),processInstanceCount);
    }

    /**
     * 拼接审批详情的最终数据
     * <p>
     * 主要是，拼接审批人的用户信息、部门信息
     */
    private BpmApprovalDetailDTO buildApprovalDetail(BpmApprovalDetailSearchDTO reqVO,
                                                        BpmnModel bpmnModel,
                                                        ProcessDefinition processDefinition,
                                                        BpmProcessDefinitionInfoPO processDefinitionInfo,
                                                        HistoricProcessInstance processInstance,
                                                        Integer processInstanceStatus,
                                                        List<BpmApprovalDetailDTO.ActivityNode> endApprovalNodeInfos,
                                                        List<BpmApprovalDetailDTO.ActivityNode> runningApprovalNodeInfos,
                                                        List<BpmApprovalDetailDTO.ActivityNode> simulateApprovalNodeInfos,
                                                        BpmTaskDTO todoTask) {
        // 1. 获取所有需要读取用户信息的 userIds
        List<BpmApprovalDetailDTO.ActivityNode> approveNodes = newArrayList(
                asList(endApprovalNodeInfos, runningApprovalNodeInfos, simulateApprovalNodeInfos));
        Set<Long> userIds = BpmProcessInstanceConvert.INSTANCE.parseUserIds(processInstance, approveNodes, todoTask);
        Map<Long, SysUserDTO> userMap = sysUserService.getUserMap(userIds);
        Map<Long, SysDeptDTO> deptMap = sysDeptService.getDeptMap(convertSet(userMap.values(), SysUserDTO::getDeptId));

        // 2. 表单权限
        String taskId = reqVO.getTaskId() == null && todoTask != null ? todoTask.getId() : reqVO.getTaskId();
        Map<String, String> formFieldsPermission = getFormFieldsPermission(bpmnModel, reqVO.getActivityId(), taskId);

        // 3. 拼接数据
        return BpmProcessInstanceConvert.INSTANCE.buildApprovalDetail(bpmnModel, processDefinition,
                processDefinitionInfo, processInstance,
                processInstanceStatus, approveNodes, todoTask, formFieldsPermission, userMap, deptMap);
    }

    /**
     * 获得【已结束】的活动节点们
     */
    private List<BpmApprovalDetailDTO.ActivityNode> getEndActivityNodeList(Long startUserId, BpmnModel bpmnModel,
                                                                           BpmProcessDefinitionInfoPO processDefinitionInfo,
                                                                           HistoricProcessInstance historicProcessInstance, Integer processInstanceStatus,
                                                                           List<HistoricActivityInstance> activities, List<HistoricTaskInstance> tasks) {
        // 遍历 tasks 列表，只处理已结束的 UserTask
        // 为什么不通过 activities 呢？因为，加签场景下，它只存在于 tasks，没有 activities，导致如果遍历 activities 的话，它无法成为一个节点
        List<HistoricTaskInstance> endTasks = filterList(tasks, task -> task.getEndTime() != null);
        List<BpmApprovalDetailDTO.ActivityNode> approvalNodes = convertList(endTasks, task -> {
            FlowElement flowNode = BpmnModelUtils.getFlowElementById(bpmnModel, task.getTaskDefinitionKey());
            BpmApprovalDetailDTO.ActivityNode activityNode = new BpmApprovalDetailDTO.ActivityNode().setId(task.getTaskDefinitionKey()).setName(task.getName())
                    .setNodeType(START_USER_NODE_ID.equals(task.getTaskDefinitionKey())
                            ? BpmSimpleModelNodeTypeEnum.START_USER_NODE.getType()
                            : ObjUtil.defaultIfNull(parseNodeType(flowNode), // 目的：解决“办理节点”的识别
                            BpmSimpleModelNodeTypeEnum.APPROVE_NODE.getType()))
                    .setStatus(getEndActivityNodeStatus(task))
                    .setCandidateStrategy(BpmnModelUtils.parseCandidateStrategy(flowNode))
                    .setStartTime(task.getCreateTime()).setEndTime(task.getEndTime())
                    .setTasks(singletonList(BpmProcessInstanceConvert.INSTANCE.buildApprovalTaskInfo(task)));
            // 如果是取消状态，则跳过
            if (BpmTaskStatusEnum.isCancelStatus(activityNode.getStatus())) {
                return null;
            }
            return activityNode;
        });

        // 遍历 activities，只处理已结束的 StartEvent、EndEvent
        List<HistoricActivityInstance> endActivities = filterList(activities, activity -> activity.getEndTime() != null
                && (StrUtil.equalsAny(activity.getActivityType(), ELEMENT_EVENT_START, ELEMENT_CALL_ACTIVITY, ELEMENT_EVENT_END)));
        endActivities.forEach(activity -> {
            // StartEvent：只处理 BPMN 的场景。因为，SIMPLE 情况下，已经有 START_USER_NODE 节点
            if (ELEMENT_EVENT_START.equals(activity.getActivityType())
                    && BpmModelTypeEnum.BPMN.getType().equals(processDefinitionInfo.getModelType())
                    && !CollUtil.contains(activities, // 特殊：如果已经存在用户手动创建的 START_USER_NODE_ID 节点，则忽略 StartEvent
                    historicActivity -> historicActivity.getActivityId().equals(START_USER_NODE_ID))) {
                BpmApprovalDetailDTO.ActivityNodeTask startTask = new BpmApprovalDetailDTO.ActivityNodeTask().setId(BpmnModelConstants.START_USER_NODE_ID)
                        .setAssignee(startUserId).setStatus(BpmTaskStatusEnum.APPROVE.getStatus());
                BpmApprovalDetailDTO.ActivityNode startNode = new BpmApprovalDetailDTO.ActivityNode().setId(startTask.getId())
                        .setName(BpmSimpleModelNodeTypeEnum.START_USER_NODE.getName())
                        .setNodeType(BpmSimpleModelNodeTypeEnum.START_USER_NODE.getType())
                        .setStatus(startTask.getStatus()).setTasks(ListUtil.of(startTask))
                        .setStartTime(activity.getStartTime())
                        .setEndTime(activity.getEndTime());
                approvalNodes.add(0, startNode);
                return;
            }
            // EndEvent
            if (ELEMENT_EVENT_END.equals(activity.getActivityType())) {
                if (BpmProcessInstanceStatusEnum.isRejectStatus(processInstanceStatus)) {
                    // 拒绝情况下，不需要展示 EndEvent 结束节点。原因是：前端已经展示 x 效果，无需重复展示
                    return;
                }
                BpmApprovalDetailDTO.ActivityNode endNode = new BpmApprovalDetailDTO.ActivityNode().setId(activity.getId())
                        .setName(BpmSimpleModelNodeTypeEnum.END_NODE.getName())
                        .setNodeType(BpmSimpleModelNodeTypeEnum.END_NODE.getType()).setStatus(processInstanceStatus)
                        .setStartTime(activity.getStartTime())
                        .setEndTime(activity.getEndTime());
                String reason = FlowableUtils.getProcessInstanceReason(historicProcessInstance);
                if (StrUtil.isNotEmpty(reason)) {
                    endNode.setTasks(singletonList(new BpmApprovalDetailDTO.ActivityNodeTask().setId(endNode.getId())
                            .setStatus(endNode.getStatus()).setReason(reason)));
                }
                approvalNodes.add(endNode);
            }
            // CallActivity
            if (ELEMENT_CALL_ACTIVITY.equals(activity.getActivityType())) {
                BpmApprovalDetailDTO.ActivityNode callActivity = new BpmApprovalDetailDTO.ActivityNode().setId(activity.getId())
                        .setName(BpmSimpleModelNodeTypeEnum.CHILD_PROCESS.getName())
                        .setNodeType(BpmSimpleModelNodeTypeEnum.CHILD_PROCESS.getType()).setStatus(processInstanceStatus)
                        .setStartTime(activity.getStartTime())
                        .setEndTime(activity.getEndTime())
                        .setProcessInstanceId(activity.getCalledProcessInstanceId());
                approvalNodes.add(callActivity);
            }
        });

        // 按照时间排序
        approvalNodes.sort(Comparator.comparing(BpmApprovalDetailDTO.ActivityNode::getStartTime));
        return approvalNodes;
    }

    /**
     * 获取结束节点的状态
     */
    private Integer getEndActivityNodeStatus(HistoricTaskInstance task) {
        Integer status = FlowableUtils.getTaskStatus(task);
        if (status != null) {
            return status;
        }
        // 结束节点未获取到状态，为跳过状态。可见 bpmn 或者 simple 的 skipExpression
        return BpmTaskStatusEnum.SKIP.getStatus();
    }

    /**
     * 获得【进行中】的活动节点们
     */
    private List<BpmApprovalDetailDTO.ActivityNode> getRunApproveNodeList(Long startUserId,
                                                                          BpmnModel bpmnModel,
                                                                          ProcessDefinition processDefinition,
                                                                          Map<String, Object> processVariables,
                                                                          List<HistoricActivityInstance> activities,
                                                                          List<HistoricTaskInstance> tasks) {
        // 构建运行中的任务、子流程，基于 activityId 分组
        List<HistoricActivityInstance> runActivities = filterList(activities, activity -> activity.getEndTime() == null
                && (StrUtil.equalsAny(activity.getActivityType(), ELEMENT_TASK_USER, ELEMENT_CALL_ACTIVITY)));
        Map<String, List<HistoricActivityInstance>> runningTaskMap = convertMultiMap(runActivities,
                HistoricActivityInstance::getActivityId);

        // 按照 activityId 分组，构建 ApprovalNodeInfo 节点
        Map<String, HistoricTaskInstance> taskMap = convertMap(tasks, HistoricTaskInstance::getId);
        return convertList(runningTaskMap.entrySet(), entry -> {
            String activityId = entry.getKey();
            List<HistoricActivityInstance> taskActivities = entry.getValue();
            // 构建活动节点
            FlowElement flowNode = BpmnModelUtils.getFlowElementById(bpmnModel, activityId);
            HistoricActivityInstance firstActivity = CollUtil.getFirst(taskActivities); // 取第一个任务，会签/或签的任务，开始时间相同
            BpmApprovalDetailDTO.ActivityNode activityNode = new BpmApprovalDetailDTO.ActivityNode().setId(firstActivity.getActivityId())
                    .setName(firstActivity.getActivityName())
                    .setNodeType(ObjUtil.defaultIfNull(parseNodeType(flowNode), // 目的：解决“办理节点”和"子流程"的识别
                            BpmSimpleModelNodeTypeEnum.APPROVE_NODE.getType()))
                    .setStatus(BpmTaskStatusEnum.RUNNING.getStatus())
                    .setCandidateStrategy(BpmnModelUtils.parseCandidateStrategy(flowNode))
                    .setStartTime(CollUtil.getFirst(taskActivities).getStartTime())
                    .setTasks(new ArrayList<>());
            // 处理每个任务的 tasks 属性
            for (HistoricActivityInstance activity : taskActivities) {
                HistoricTaskInstance task = taskMap.get(activity.getTaskId());
                // 特殊情况：子流程节点 ChildProcess 仅存在于 activity 中，并且没有自身的 task，需要跳过执行
                // TODO @芋艿：后续看看怎么优化！
                if (task == null) {
                    continue;
                }
                activityNode.getTasks().add(BpmProcessInstanceConvert.INSTANCE.buildApprovalTaskInfo(task));
                // 加签子任务，需要过滤掉已经完成的加签子任务
                List<HistoricTaskInstance> childrenTasks = filterList(
                        taskService.getAllChildrenTaskListByParentTaskId(activity.getTaskId(), tasks),
                        childTask -> childTask.getEndTime() == null);
                if (CollUtil.isNotEmpty(childrenTasks)) {
                    activityNode.getTasks().addAll(
                            convertList(childrenTasks, BpmProcessInstanceConvert.INSTANCE::buildApprovalTaskInfo));
                }
            }
            // 处理每个任务的 candidateUsers 属性：如果是依次审批，需要预测它的后续审批人。因为 Task 是审批完一个，创建一个新的 Task
            if (BpmnModelUtils.isSequentialUserTask(flowNode)) {
                List<Long> candidateUserIds = getTaskCandidateUserList(bpmnModel, flowNode.getId(),
                        startUserId, processDefinition.getId(), processVariables);
                // 截取当前审批人位置后面的候选人，不包含当前审批人
                BpmApprovalDetailDTO.ActivityNodeTask approvalTaskInfo = CollUtil.getFirst(activityNode.getTasks());
                Assert.notNull(approvalTaskInfo, "任务不能为空");
                int index = CollUtil.indexOf(candidateUserIds,
                        userId -> ObjectUtils.equalsAny(userId, approvalTaskInfo.getOwner(),
                                approvalTaskInfo.getAssignee())); // 委派或者向前加签情况，需要先比较 owner
                activityNode.setCandidateUserIds(CollUtil.sub(candidateUserIds, index + 1, candidateUserIds.size()));
            }
            if (BpmSimpleModelNodeTypeEnum.CHILD_PROCESS.getType().equals(activityNode.getNodeType())) {
                activityNode.setProcessInstanceId(firstActivity.getCalledProcessInstanceId());
            }
            return activityNode;
        });
    }

    /**
     * 获得【预测（未来）】的活动节点们
     */
    private List<BpmApprovalDetailDTO.ActivityNode> getSimulateApproveNodeList(Long startUserId, BpmnModel bpmnModel,
                                                                               BpmProcessDefinitionInfoPO processDefinitionInfo,
                                                                               Map<String, Object> processVariables,
                                                                               List<HistoricActivityInstance> activities,
                                                                               Set<String> needSimulateTaskDefKeysByReturn) {
        // TODO @芋艿：【可优化】在驳回场景下，未来的预测准确性不高。原因是，驳回后，HistoricActivityInstance
        // 包括了历史的操作，不是只有 startEvent 到当前节点的记录
        Set<String> runActivityIds = convertSet(activities, HistoricActivityInstance::getActivityId);
        // 情况一：BPMN 设计器
        if (Objects.equals(BpmModelTypeEnum.BPMN.getType(), processDefinitionInfo.getModelType())) {
            List<FlowElement> flowElements = BpmnModelUtils.simulateProcess(bpmnModel, processVariables);
            return convertList(flowElements, flowElement -> buildNotRunApproveNodeForBpmn(
                    startUserId, bpmnModel, flowElements,
                    processDefinitionInfo, processVariables, flowElement, runActivityIds, needSimulateTaskDefKeysByReturn));
        }
        // 情况二：SIMPLE 设计器
//        if (Objects.equals(BpmModelTypeEnum.SIMPLE.getType(), processDefinitionInfo.getModelType())) {
//            BpmSimpleModelNodeDTO simpleModel = JsonUtils.parseObject(processDefinitionInfo.getSimpleModel(),
//                    BpmSimpleModelNodeDTO.class);
//            List<BpmSimpleModelNodeDTO> simpleNodes = SimpleModelUtils.simulateProcess(simpleModel, processVariables);
//            return convertList(simpleNodes, simpleNode -> buildNotRunApproveNodeForSimple(
//                    startUserId, bpmnModel,
//                    processDefinitionInfo, processVariables, simpleNode, runActivityIds, needSimulateTaskDefKeysByReturn));
//        }
        throw new IllegalArgumentException("未知设计器类型：" + processDefinitionInfo.getModelType());
    }

//    private ActivityNode buildNotRunApproveNodeForSimple(Long startUserId, BpmnModel bpmnModel,
//                                                         BpmProcessDefinitionInfoPO processDefinitionInfo, Map<String, Object> processVariables,
//                                                         BpmSimpleModelNodeDTO node, Set<String> runActivityIds,
//                                                         Set<String> needSimulateTaskDefKeysByReturn) {
//        // TODO @芋艿：【可优化】在驳回场景下，未来的预测准确性不高。原因是，驳回后，HistoricActivityInstance
//        // 包括了历史的操作，不是只有 startEvent 到当前节点的记录
//        if (runActivityIds.contains(node.getId())
//                && !needSimulateTaskDefKeysByReturn.contains(node.getId())) { // 特殊：回退操作时候，会记录需要预测的节点到流程变量中。即使在历史操作中，也需要预测
//            return null;
//        }
//        Integer status = BpmTaskStatusEnum.NOT_START.getStatus();
//        // 如果节点被跳过。设置状态为跳过
//        if (SimpleModelUtils.isSkipNode(node, processVariables)) {
//            status = BpmTaskStatusEnum.SKIP.getStatus();
//        }
//        ActivityNode activityNode = new ActivityNode().setId(node.getId()).setName(node.getName())
//                .setNodeType(node.getType()).setCandidateStrategy(node.getCandidateStrategy())
//                .setStatus(status);
//
//        // 1. 开始节点/审批节点
//        if (ObjectUtils.equalsAny(node.getType(),
//                BpmSimpleModelNodeTypeEnum.START_USER_NODE.getType(),
//                BpmSimpleModelNodeTypeEnum.APPROVE_NODE.getType(),
//                BpmSimpleModelNodeTypeEnum.TRANSACTOR_NODE.getType())) {
//            List<Long> candidateUserIds = getTaskCandidateUserList(bpmnModel, node.getId(),
//                    startUserId, processDefinitionInfo.getProcessDefinitionId(), processVariables);
//            activityNode.setCandidateUserIds(candidateUserIds);
//            return activityNode;
//        }
//
//        // 2. 结束节点
//        if (BpmSimpleModelNodeTypeEnum.END_NODE.getType().equals(node.getType())) {
//            return activityNode;
//        }
//
//        // 3. 抄送节点
//        if (CollUtil.isEmpty(runActivityIds) && // 流程发起时：需要展示抄送节点，用于选择抄送人
//                BpmSimpleModelNodeTypeEnum.COPY_NODE.getType().equals(node.getType())) {
//            List<Long> candidateUserIds = getTaskCandidateUserList(bpmnModel, node.getId(),
//                    startUserId, processDefinitionInfo.getProcessDefinitionId(), processVariables);
//            activityNode.setCandidateUserIds(candidateUserIds);
//            return activityNode;
//        }
//
//        // 4. 子流程节点
//        if (BpmSimpleModelNodeTypeEnum.CHILD_PROCESS.getType().equals(node.getType())) {
//            return activityNode;
//        }
//        return null;
//    }

    private BpmApprovalDetailDTO.ActivityNode buildNotRunApproveNodeForBpmn(Long startUserId, BpmnModel bpmnModel, List<FlowElement> flowElements,
                                                                            BpmProcessDefinitionInfoPO processDefinitionInfo,
                                                                            Map<String, Object> processVariables,
                                                                            FlowElement node, Set<String> runActivityIds,
                                                                            Set<String> needSimulateTaskDefKeysByReturn) {
        // 回退操作时候，会记录需要预测的节点到流程变量中。即使节点在历史操作中，也需要预测。
        if (!needSimulateTaskDefKeysByReturn.contains(node.getId()) && runActivityIds.contains(node.getId())) {
            return null;
        }

        Integer status = BpmTaskStatusEnum.NOT_START.getStatus();
        // 如果节点被跳过，状态设置为跳过
        if (BpmnModelUtils.isSkipNode(node, processVariables)) {
            status = BpmTaskStatusEnum.SKIP.getStatus();
        }
        BpmApprovalDetailDTO.ActivityNode activityNode = new BpmApprovalDetailDTO.ActivityNode().setId(node.getId())
                .setStatus(status);

        // 1. 开始节点
        if (node instanceof StartEvent) {
            if (CollUtil.contains(flowElements, // 特殊：如果已经存在用户手动创建的 START_USER_NODE_ID 节点，则忽略 StartEvent
                    flowElement -> flowElement.getId().equals(START_USER_NODE_ID))) {
                return null;
            }
            return activityNode.setName(BpmSimpleModelNodeTypeEnum.START_USER_NODE.getName())
                    .setNodeType(BpmSimpleModelNodeTypeEnum.START_USER_NODE.getType());
        }

        // 2. 审批节点
        if (node instanceof UserTask) {
            List<Long> candidateUserIds = getTaskCandidateUserList(bpmnModel, node.getId(),
                    startUserId, processDefinitionInfo.getProcessDefinitionId(), processVariables);
            return activityNode.setName(node.getName()).setNodeType(BpmSimpleModelNodeTypeEnum.APPROVE_NODE.getType())
                    .setCandidateStrategy(BpmnModelUtils.parseCandidateStrategy(node))
                    .setCandidateUserIds(candidateUserIds);
        }

        // 3. 结束节点
        if (node instanceof EndEvent) {
            return activityNode.setName(BpmSimpleModelNodeTypeEnum.END_NODE.getName())
                    .setNodeType(BpmSimpleModelNodeTypeEnum.END_NODE.getType());
        }
        return null;
    }

    private List<Long> getTaskCandidateUserList(BpmnModel bpmnModel, String activityId,
                                                Long startUserId, String processDefinitionId, Map<String, Object> processVariables) {
        Set<Long> userIds = taskCandidateInvoker.calculateUsersByActivity(bpmnModel, activityId,
                startUserId, processDefinitionId, processVariables);
        return new ArrayList<>(userIds);
    }

    @Override
    public BpmProcessInstanceBpmnModelViewDTO getProcessInstanceBpmnModelView(String id) {
        // 1.1 获得流程实例
        HistoricProcessInstance processInstance = getHistoricProcessInstance(id);
        if (processInstance == null) {
            return null;
        }
        // 1.2 获得流程定义
        BpmnModel bpmnModel = processDefinitionService
                .getProcessDefinitionBpmnModel(processInstance.getProcessDefinitionId());
        if (bpmnModel == null) {
            return null;
        }
//        BpmSimpleModelNodeDTO simpleModel = null;
//        BpmProcessDefinitionInfoPO processDefinitionInfo = processDefinitionService.getProcessDefinitionInfo(
//                processInstance.getProcessDefinitionId());
//        if (processDefinitionInfo != null
//                && BpmModelTypeEnum.SIMPLE.getType().equals(processDefinitionInfo.getModelType())) {
//            simpleModel = JsonUtils.parseObject(processDefinitionInfo.getSimpleModel(), BpmSimpleModelNodeDTO.class);
//        }
        // 1.3 获得流程实例对应的活动实例列表 + 任务列表
        List<HistoricActivityInstance> activities = taskService.getActivityListByProcessInstanceId(id);
        List<HistoricTaskInstance> tasks = taskService.getTaskListByProcessInstanceId(id, true);

        // 2.1 拼接进度信息
        Set<String> unfinishedTaskActivityIds = convertSet(activities, HistoricActivityInstance::getActivityId,
                activityInstance -> activityInstance.getEndTime() == null);
        Set<String> finishedTaskActivityIds = convertSet(activities, HistoricActivityInstance::getActivityId,
                activityInstance -> activityInstance.getEndTime() != null
                        && ObjectUtil.notEqual(activityInstance.getActivityType(),
                        BpmnXMLConstants.ELEMENT_SEQUENCE_FLOW));
        Set<String> finishedSequenceFlowActivityIds = convertSet(activities, HistoricActivityInstance::getActivityId,
                activityInstance -> activityInstance.getEndTime() != null
                        && ObjectUtil.equals(activityInstance.getActivityType(),
                        BpmnXMLConstants.ELEMENT_SEQUENCE_FLOW));
        // 特殊：会签情况下，会有部分已完成（审批）、部分未完成（待审批），此时需要 finishedTaskActivityIds 移除掉
        finishedTaskActivityIds.removeAll(unfinishedTaskActivityIds);
        // 特殊：如果流程实例被拒绝，则需要计算是哪个活动节点。
        // 注意，只取最后一个。因为会存在多次拒绝的情况，拒绝驳回到指定节点
        Set<String> rejectTaskActivityIds = CollUtil.newHashSet();
        if (BpmProcessInstanceStatusEnum.isRejectStatus(FlowableUtils.getProcessInstanceStatus(processInstance))) {
            tasks.stream()
                    .filter(task -> BpmTaskStatusEnum.isRejectStatus(FlowableUtils.getTaskStatus(task)))
                    .max(Comparator.comparing(HistoricTaskInstance::getEndTime))
                    .ifPresent(reject -> rejectTaskActivityIds.add(reject.getTaskDefinitionKey()));
            finishedTaskActivityIds.removeAll(rejectTaskActivityIds);
        }

        // 2.2 拼接基础信息
        Set<Long> userIds = BpmProcessInstanceConvert.INSTANCE.parseUserIds02(processInstance, tasks);
        Map<Long, SysUserDTO> userMap = sysUserService.getUserMap(userIds);
        Map<Long, SysDeptDTO> deptMap = sysDeptService.getDeptMap(convertSet(userMap.values(), SysUserDTO::getDeptId));
        return BpmProcessInstanceConvert.INSTANCE.buildProcessInstanceBpmnModelView(processInstance, tasks, bpmnModel,
                unfinishedTaskActivityIds, finishedTaskActivityIds, finishedSequenceFlowActivityIds,
                rejectTaskActivityIds,
                userMap, deptMap);
    }

    // ========== Update 写入相关方法 ==========

    /**
     * 创建流程实例（提供给前端）
     *
     * @param userId      用户编号
     * @param createReqVO 创建信息
     * @return 实例的编号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProcessInstance createProcessInstance(Long userId, BpmProcessInstanceDTO createReqVO) {
        // 获得流程定义
        ProcessDefinition definition = processDefinitionService
                .getProcessDefinition(createReqVO.getProcessDefinitionId());
        // 发起流程
        return createProcessInstance0(userId, definition, createReqVO);
    }

//    /**
//     * 创建流程实例（提供给内部）
//     *
//     * @param userId       用户编号
//     * @param createReqDTO 创建信息
//     * @return 实例的编号
//     */
//    @Override
//    public String createProcessInstance(Long userId, @Valid BpmProcessInstanceCreateReqDTO createReqDTO) {
//        return FlowableUtils.executeAuthenticatedUserId(userId, () -> {
//            // 获得流程定义
//            ProcessDefinition definition = processDefinitionService
//                    .getActiveProcessDefinition(createReqDTO.getProcessDefinitionKey());
//            // 发起流程
//            return createProcessInstance0(userId, definition, createReqDTO.getVariables(),
//                    createReqDTO.getBusinessKey(),
//                    createReqDTO.getStartUserSelectAssignees());
//        });
//    }

    // 发起流程
    private ProcessInstance createProcessInstance0(Long userId, ProcessDefinition definition,BpmProcessInstanceDTO createReqVO) {

        Map<String, Object> variables = createReqVO.getVariables();
        String businessKey = null;
        if (!StringUtil.isEmpty(createReqVO.getBusinessId())) {
           businessKey = createReqVO.getBusinessId().toString();
        }
        Map<String, List<Long>> startUserSelectAssignees = createReqVO.getStartUserSelectAssignees();

        // 1.1 校验流程定义
        if (definition == null) {
            throw exception(PROCESS_DEFINITION_NOT_EXISTS);
        }
        if (definition.isSuspended()) {
            throw exception(PROCESS_DEFINITION_IS_SUSPENDED);
        }
        BpmProcessDefinitionInfoPO processDefinitionInfo = processDefinitionService
                .getProcessDefinitionInfo(definition.getId());
        processDefinitionInfo.setFieldList(processDefinitionInfo.getFieldKeys());
        if (processDefinitionInfo == null) {
            throw exception(PROCESS_DEFINITION_NOT_EXISTS);
        }
        // 1.2 校验是否能够发起
        if (!processDefinitionService.canUserStartProcessDefinition(processDefinitionInfo, userId)) {
            throw exception(PROCESS_INSTANCE_START_USER_CAN_START);
        }
        // 1.3 校验发起人自选审批人
        validateStartUserSelectAssignees(userId, definition, startUserSelectAssignees, variables);

        // 2. 创建流程实例
        if (variables == null) {
            variables = new HashMap<>();
        }
        FlowableUtils.filterProcessInstanceFormVariable(variables); // 过滤一下，避免 ProcessInstance 系统级的变量被占用
        variables.put(BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_START_USER_ID, userId); // 设置流程变量，发起人 ID
        variables.put(BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_STATUS, // 流程实例状态：审批中
                BpmProcessInstanceStatusEnum.RUNNING.getStatus());
        variables.put(BpmnVariableConstants.PROCESS_INSTANCE_SKIP_EXPRESSION_ENABLED, true); // 跳过表达式需要添加此变量为 true，不影响没配置 skipExpression 的节点
        if (CollUtil.isNotEmpty(startUserSelectAssignees)) {
            // 设置流程变量，发起人自选审批人
            variables.put(BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_START_USER_SELECT_ASSIGNEES,
                    startUserSelectAssignees);
        }
        // 显式设置引擎的认证用户
        String currentUserId = String.valueOf(userId);
        identityService.setAuthenticatedUserId(currentUserId);
        try {
            // 3. 创建流程
            ProcessInstanceBuilder processInstanceBuilder = runtimeService.createProcessInstanceBuilder()
                    .processDefinitionId(definition.getId())
                    .businessKey(businessKey)
                    .variables(variables);
            // 3.1 创建流程 ID
            BpmModelMetaInfoDTO.ProcessIdRule processIdRule = processDefinitionInfo.getProcessIdRule();
            if (processIdRule != null && Boolean.TRUE.equals(processIdRule.getEnable())) {
                processInstanceBuilder.predefineProcessInstanceId(processIdRedisDAO.generate(processIdRule));
            }
            // 3.2 流程名称
            processInstanceBuilder.name(generateProcessInstanceName(userId, definition, processDefinitionInfo, variables));
            // 3.3 发起流程实例
            ProcessInstance instance = processInstanceBuilder.start();

            // 新增业务与实例关联数据
            if (!StringUtil.isEmpty(createReqVO.getBusinessId()) && !StringUtil.isEmpty(createReqVO.getBusinessDataId())) {
                // 4. 获取当前活跃的任务节点
                List<Task> activeTasks = taskService.getRunningTaskListByProcessInstanceId(instance.getId(), null, null);
                // 5. 解析当前节点名称和审批人名称
                String currentNodeName = "";
                String approverNames = "";

                if (CollUtil.isNotEmpty(activeTasks)) {
                    // 5.1 拼接节点名称
                    currentNodeName = activeTasks.stream()
                            .map(Task::getName)
                            .filter(StrUtil::isNotBlank)
                            .distinct()
                            .collect(Collectors.joining(","));

                    // 5.2 解析审批人并将ID转为昵称)
                    Set<Long> assigneeIds = activeTasks.stream()
                            .map(Task::getAssignee)
                            .filter(StrUtil::isNotBlank) // 过滤掉未分配的任务（例如候选组模式且未签收）
                            .map(NumberUtils::parseLong)
                            .collect(Collectors.toSet());

                    if (CollUtil.isNotEmpty(assigneeIds)) {
                        List<SysUserDTO> users = sysUserService.getUserList(assigneeIds);
                        if (CollUtil.isNotEmpty(users)) {
                            approverNames = users.stream()
                                    .map(SysUserDTO::getUserName) // 假设 SysUserDTO 中是 getNickname 或 getUserName
                                    .filter(StrUtil::isNotBlank)
                                    .collect(Collectors.joining(","));
                        }
                    }

                    // 如果没有 Assignee，可能是候选人模式，暂时显示"未分配具体用户"
                    if (StrUtil.isBlank(approverNames) && CollUtil.isNotEmpty(activeTasks)) {
                        approverNames = "待定";
                    }
                }

                // 6. 保存业务与流程实例关联表 (BPM_BUSINESS_INSTANCE)
                BpmBusinessInstanceDTO instanceDO = new BpmBusinessInstanceDTO();
                instanceDO.setId(snowflake.nextId());
                instanceDO.setBusinessDataId(createReqVO.getBusinessDataId());
                instanceDO.setBusinessId(createReqVO.getBusinessId());
                instanceDO.setProcInstId(instance.getId());
                instanceDO.setProcDefId(instance.getProcessDefinitionId());
                instanceDO.setInstanceStatus("1");
                instanceDO.setStartTime(new Date());
                instanceDO.setCurrentNodeName(currentNodeName);
                instanceDO.setApproverNames(approverNames);

                bpmBusinessInstanceMapper.insert(instanceDO);
            }
            return instance;
        }finally {
            // 清理现场，防止影响后续操作
            identityService.setAuthenticatedUserId(null);
        }
    }

    // 校验发起人自选审批人
    private void validateStartUserSelectAssignees(Long userId, ProcessDefinition definition,
                                                  Map<String, List<Long>> startUserSelectAssignees,
                                                  Map<String, Object> variables) {
        // 1. 获取预测的节点信息
        BpmApprovalDetailDTO detailRespVO = getApprovalDetail(userId, new BpmApprovalDetailSearchDTO()
                .setProcessDefinitionId(definition.getId())
                .setProcessVariables(variables));
        List<BpmApprovalDetailDTO.ActivityNode> activityNodes = detailRespVO.getActivityNodes();
        if (CollUtil.isEmpty(activityNodes)) {
            return;
        }

        // 2.1 移除掉不是发起人自选审批人节点
        activityNodes.removeIf(task ->
                ObjectUtil.notEqual(BpmTaskCandidateStrategyEnum.START_USER_SELECT.getStrategy(), task.getCandidateStrategy()));
        // 2.2 流程发起时要先获取当前流程的预测走向节点，发起时只校验预测的节点发起人自选审批人的审批人和抄送人是否都配置了
        activityNodes.forEach(task -> {
            List<Long> assignees = startUserSelectAssignees != null ? startUserSelectAssignees.get(task.getId()) : null;
            if (CollUtil.isEmpty(assignees)) {
                throw exception(PROCESS_INSTANCE_START_USER_SELECT_ASSIGNEES_NOT_CONFIG, task.getName());
            }
            Map<Long, SysUserDTO> userMap = sysUserService.getUserMap(assignees);
            assignees.forEach(assignee -> {
                if (userMap.get(assignee) == null) {
                    throw exception(PROCESS_INSTANCE_START_USER_SELECT_ASSIGNEES_NOT_EXISTS, task.getName(), assignee);
                }
            });
        });
    }

    // 获取流程名称
    private String generateProcessInstanceName(Long userId,
                                               ProcessDefinition definition,
                                               BpmProcessDefinitionInfoPO definitionInfo,
                                               Map<String, Object> variables) {
        if (definition == null || definitionInfo == null) {
            return null;
        }
        BpmModelMetaInfoDTO.TitleSetting titleSetting = definitionInfo.getTitleSetting();
        if (titleSetting == null || !BooleanUtil.isTrue(titleSetting.getEnable())) {
            return definition.getName();
        }
        SysUserDTO user = sysUserService.getById(userId);
        Map<String, Object> cloneVariables = new HashMap<>(variables);
        cloneVariables.put(BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_START_USER_ID, user.getUserName());
        cloneVariables.put(BpmnVariableConstants.PROCESS_START_TIME, DateUtil.now());
        cloneVariables.put(BpmnVariableConstants.PROCESS_DEFINITION_NAME, definition.getName().trim());
        return StrUtil.format(definitionInfo.getTitleSetting().getTitle(), cloneVariables);
    }

    /**
     * 发起人取消流程实例
     *
     * @param userId      用户编号
     * @param cancelReqVO 取消信息
     */
    @Override
    public void cancelProcessInstanceByStartUser(Long userId, BpmProcessInstanceCancelDTO cancelReqVO) {
        // 1.1 校验流程实例存在
        ProcessInstance instance = getProcessInstance(cancelReqVO.getId());
        if (instance == null) {
            throw exception(PROCESS_INSTANCE_CANCEL_FAIL_NOT_EXISTS);
        }
        // 1.2 只能取消自己的
        if (!Objects.equals(instance.getStartUserId(), String.valueOf(userId))) {
            throw exception(PROCESS_INSTANCE_CANCEL_FAIL_NOT_SELF);
        }
        // 1.3 校验允许撤销审批中的申请
        BpmProcessDefinitionInfoPO processDefinitionInfo = processDefinitionService
                .getProcessDefinitionInfo(instance.getProcessDefinitionId());
        Assert.notNull(processDefinitionInfo, "流程定义({})不存在", processDefinitionInfo);
        if (processDefinitionInfo.getAllowCancelRunningProcess() != null // 防止未配置 AllowCancelRunningProcess , 默认为可取消
                && BooleanUtil.isFalse(processDefinitionInfo.getAllowCancelRunningProcess())) {
            throw exception(PROCESS_INSTANCE_CANCEL_FAIL_NOT_ALLOW);
        }
        // 1.4 子流程不允许取消
        if (StrUtil.isNotBlank(instance.getSuperExecutionId())) {
            throw exception(PROCESS_INSTANCE_CANCEL_CHILD_FAIL_NOT_ALLOW);
        }

        // 2. 取消流程
        updateProcessInstanceCancel(cancelReqVO.getId(),
                BpmReasonEnum.CANCEL_PROCESS_INSTANCE_BY_START_USER.format(cancelReqVO.getReason()));
    }

    /**
     * 管理员取消流程实例
     *
     * @param userId      用户编号
     * @param cancelReqVO 取消信息
     */
    @Override
    public void cancelProcessInstanceByAdmin(Long userId, BpmProcessInstanceCancelDTO cancelReqVO) {
        // 1.1 校验流程实例存在
        ProcessInstance instance = getProcessInstance(cancelReqVO.getId());
        if (instance == null) {
            throw exception(PROCESS_INSTANCE_CANCEL_FAIL_NOT_EXISTS);
        }

        // 2. 取消流程
        SysUserDTO user = sysUserService.getById(userId);
        updateProcessInstanceCancel(cancelReqVO.getId(),
                BpmReasonEnum.CANCEL_PROCESS_INSTANCE_BY_ADMIN.format(user.getUserName(), cancelReqVO.getReason()));
    }

    // 取消流程
    private void updateProcessInstanceCancel(String id, String reason) {
        // 1. 更新流程实例 status
        runtimeService.setVariable(id, BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_STATUS,
                BpmProcessInstanceStatusEnum.CANCEL.getStatus());
        runtimeService.setVariable(id, BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_REASON, reason);

        // 2. 取消所有子流程
        List<ProcessInstance> childProcessInstances = runtimeService.createProcessInstanceQuery()
                .superProcessInstanceId(id).list();
        childProcessInstances.forEach(processInstance -> updateProcessInstanceCancel(
                processInstance.getProcessInstanceId(), BpmReasonEnum.CANCEL_CHILD_PROCESS_INSTANCE_BY_MAIN_PROCESS.getReason()));

        // 3. 结束流程
        taskService.moveTaskToEnd(id, reason);
    }

    @Override
    public void updateProcessInstanceReject(ProcessInstance processInstance, String reason) {
        runtimeService.setVariable(processInstance.getProcessInstanceId(),
                BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_STATUS,
                BpmProcessInstanceStatusEnum.REJECT.getStatus());
        runtimeService.setVariable(processInstance.getProcessInstanceId(),
                BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_REASON,
                BpmReasonEnum.REJECT_TASK.format(reason));
    }

    @Override
    public void updateProcessInstanceVariables(String id, Map<String, Object> variables) {
        runtimeService.setVariables(id, variables);
    }

    @Override
    public void removeProcessInstanceVariables(String id, Collection<String> variableNames) {
        runtimeService.removeVariables(id, variableNames);
    }

    // ========== Event 事件相关方法 ==========

    @Override
    public void processProcessInstanceCompleted(ProcessInstance instance) {
        // 1.1 获取当前状态
        Integer status = (Integer) instance.getProcessVariables()
                .get(BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_STATUS);
        String reason = (String) instance.getProcessVariables()
                .get(BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_REASON);
        // 1.2 当流程状态还是审批状态中，说明审批通过了，则变更下它的状态
        // 为什么这么处理？因为流程完成，并且完成了，说明审批通过了
        if (Objects.equals(status, BpmProcessInstanceStatusEnum.RUNNING.getStatus())) {
            status = BpmProcessInstanceStatusEnum.APPROVE.getStatus();
            runtimeService.setVariable(instance.getId(), BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_STATUS,
                    status);
        }

        // 1.3 如果子流程拒绝，设置其父流程也为拒绝状态，且结束父流程
        // 相关问题链接：https://t.zsxq.com/kZhyb
        if (Objects.equals(status, BpmProcessInstanceStatusEnum.REJECT.getStatus())
                && StrUtil.isNotBlank(instance.getSuperExecutionId())) {
            // 1.3.1 获取父流程实例 并标记为不通过
            Execution execution = runtimeService.createExecutionQuery().executionId(instance.getSuperExecutionId()).singleResult();
            ProcessInstance parentProcessInstance = getProcessInstance(execution.getProcessInstanceId());
            updateProcessInstanceReject(parentProcessInstance, BpmReasonEnum.REJECT_CHILD_PROCESS.getReason());

            // 1.3.2 结束父流程。需要在子流程结束事务提交后执行
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

                @Override
                public void afterCompletion(int transactionStatus) {
                    // 回滚情况，直接返回
                    if (ObjectUtil.equal(transactionStatus, TransactionSynchronization.STATUS_ROLLED_BACK)) {
                        return;
                    }
                    taskService.moveTaskToEnd(parentProcessInstance.getId(), BpmReasonEnum.REJECT_CHILD_PROCESS.getReason());
                }
            });
        }

        // 2. 发送对应的消息通知（短信）
//        if (Objects.equals(status, BpmProcessInstanceStatusEnum.APPROVE.getStatus())) {
//            messageService.sendMessageWhenProcessInstanceApprove(
//                    BpmProcessInstanceConvert.INSTANCE.buildProcessInstanceApproveMessage(instance));
//        } else if (Objects.equals(status, BpmProcessInstanceStatusEnum.REJECT.getStatus())) {
//            messageService.sendMessageWhenProcessInstanceReject(
//                    BpmProcessInstanceConvert.INSTANCE.buildProcessInstanceRejectMessage(instance, reason));
//        }


        // 处理业务数据与实例关联表
        // 业务表状态定义：2=通过, 3=驳回, 4=取消
        String bizStatus = "2"; // 默认为通过
        if (Objects.equals(status, BpmProcessInstanceStatusEnum.REJECT.getStatus())) {
            bizStatus = "3";
        } else if (Objects.equals(status, BpmProcessInstanceStatusEnum.CANCEL.getStatus())) {
            bizStatus = "4";
        }

        // 2. 构建更新对象
        BpmBusinessInstanceDTO bpmBusinessInstanceDTO = new BpmBusinessInstanceDTO();
        bpmBusinessInstanceDTO.setProcInstId(instance.getId());
        bpmBusinessInstanceDTO.setInstanceStatus(bizStatus);
        bpmBusinessInstanceDTO.setEndTime(new Date()); // 记录结束时间
        bpmBusinessInstanceDTO.setCurrentNodeName("已结束"); // 流程结束，当前节点可置空或标记为结束
        bpmBusinessInstanceDTO.setApproverNames("无");

        // 3. 执行更新
        bpmBusinessInstanceMapper.updateByProcInstId(bpmBusinessInstanceDTO);



        // 3. 发送流程实例的状态事件
        processInstanceEventPublisher.sendProcessInstanceResultEvent(
                BpmProcessInstanceConvert.INSTANCE.buildProcessInstanceStatusEvent(this, instance, status, reason));

        // 4. 流程后置通知
//        if (Objects.equals(status, BpmProcessInstanceStatusEnum.APPROVE.getStatus())) {
//            BpmProcessDefinitionInfoPO processDefinitionInfo = processDefinitionService.
//                    getProcessDefinitionInfo(instance.getProcessDefinitionId());
//            if (ObjUtil.isNotNull(processDefinitionInfo) &&
//                    ObjUtil.isNotNull(processDefinitionInfo.getProcessAfterTriggerSetting())) {
//                BpmModelMetaInfoDTO.HttpRequestSetting setting = processDefinitionInfo.getProcessAfterTriggerSetting();
//
//                BpmHttpRequestUtils.executeBpmHttpRequest(instance,
//                        setting.getUrl(), setting.getHeader(), setting.getBody(), true, setting.getResponse());
//            }
//        }
    }

    /**
     * 处理 ProcessInstance 开始事件，例如说：流程前置通知
     *
     * @param instance 流程任务
     */
    @Override
    public void processProcessInstanceCreated(ProcessInstance instance) {
        BpmProcessDefinitionInfoPO processDefinitionInfo = processDefinitionService.
                getProcessDefinitionInfo(instance.getProcessDefinitionId());
        ProcessDefinition processDefinition = processDefinitionService.getProcessDefinition(instance.getProcessDefinitionId());
        if (processDefinition == null || processDefinitionInfo == null) {
            return;
        }

        // 自定义标题。目的：主要处理子流程的标题无法处理
        // 注意：必须使用 TransactionSynchronizationManager 事务提交后，否则不生效！！！
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

            @Override
            public void afterCommit() {
                String name = generateProcessInstanceName(Long.valueOf(instance.getStartUserId()),
                        processDefinition, processDefinitionInfo, instance.getProcessVariables());
                if (ObjUtil.notEqual(instance.getName(), name)) {
                    runtimeService.setProcessInstanceName(instance.getProcessInstanceId(), name);
                }

                // 流程前置通知：需要在流程启动后(事务提交后)，保证 variables 已设置
                // 相关问题链接：https://t.zsxq.com/DF7Kq
//                if (ObjUtil.isNull(processDefinitionInfo.getProcessBeforeTriggerSetting())) {
//                    return;
//                }
//                BpmModelMetaInfoDTO.HttpRequestSetting setting = processDefinitionInfo.getProcessBeforeTriggerSetting();
//                BpmHttpRequestUtils.executeBpmHttpRequest(instance,
//                        setting.getUrl(), setting.getHeader(), setting.getBody(), true, setting.getResponse());
            }

        });
    }

}
