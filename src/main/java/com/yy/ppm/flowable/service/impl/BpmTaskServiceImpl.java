package com.yy.ppm.flowable.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.*;
import cn.hutool.extra.spring.SpringUtil;
import com.yy.common.enums.WebsocketEnum;
import com.yy.common.flowable.constants.BpmMessageConstants;
import com.yy.common.flowable.constants.BpmnModelConstants;
import com.yy.common.flowable.constants.BpmnVariableConstants;
import com.yy.common.flowable.enums.*;
import com.yy.common.flowable.utils.*;
import com.yy.common.page.Pages;
import com.yy.common.util.PageConverterUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.common.ws.WebSocketUtils;
import com.yy.framework.flowable.convert.BpmTaskConvert;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.flowable.bean.dto.*;
import com.yy.ppm.flowable.bean.po.BpmFormPO;
import com.yy.ppm.flowable.bean.po.BpmProcessDefinitionInfoPO;
import com.yy.ppm.flowable.mapper.BpmBusinessInstanceMapper;
import com.yy.ppm.flowable.service.*;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import com.yy.ppm.system.bean.dto.SysRoleDTO;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import com.yy.ppm.system.service.SysDeptService;
import com.yy.ppm.system.service.SysRoleService;
import com.yy.ppm.system.service.SysUserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.*;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.yy.common.flowable.constants.BpmnModelConstants.START_USER_NODE_ID;
import static com.yy.common.flowable.constants.ErrorCodeConstants.*;
import static com.yy.common.flowable.utils.BpmnModelUtils.*;
import static com.yy.common.flowable.utils.CollectionUtils.*;
import static com.yy.common.flowable.utils.ServiceExceptionUtil.exception;

/**
 * 流程任务实例 Service 实现类
 *
 * @author 芋道源码
 * @author jason
 */
@Slf4j
@Service
public class BpmTaskServiceImpl implements BpmTaskService {

    @Resource
    private TaskService taskService;
    @Resource
    private HistoryService historyService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private ManagementService managementService;

    @Resource
    private BpmProcessInstanceService processInstanceService;
    @Resource
    private BpmProcessDefinitionService bpmProcessDefinitionService;
    @Resource
    private BpmProcessInstanceCopyService processInstanceCopyService;
    @Resource
    private BpmModelService modelService;
//    @Resource
//    private BpmMessageService messageService;
    @Resource
    private BpmFormService formService;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysDeptService sysDeptService;

    @Resource
    private BpmBusinessInstanceMapper bpmBusinessInstanceMapper;

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private SysFileService sysFileService;


    // ========== Query 查询相关方法 ==========

    /**
     * 获得待办的流程任务分页
     *
     * @param userId    用户编号
     * @param pageVO 分页请求
     * @return 流程任务分页
     */
    @Override
    public Pages<Task> getTaskTodoPage(Long userId, BpmTaskSearchDTO pageVO) {
        TaskQuery taskQuery = taskService.createTaskQuery()
                .taskAssignee(String.valueOf(userId)) // 分配给自己
                .active()
                .includeProcessVariables()
                .orderByTaskCreateTime().desc(); // 创建时间倒序
        if (StrUtil.isNotBlank(pageVO.getName())) {
            taskQuery.taskNameLike("%" + pageVO.getName() + "%");
        }
        if (StrUtil.isNotBlank(pageVO.getProcessDefinitionName())) {
            taskQuery.processDefinitionNameLike("%" + pageVO.getProcessDefinitionName() + "%");
        }
        if (StrUtil.isNotEmpty(pageVO.getCategory())) {
            taskQuery.taskCategory(pageVO.getCategory());
        }
        if (StrUtil.isNotEmpty(pageVO.getProcessDefinitionKey())) {
            taskQuery.processDefinitionKey(pageVO.getProcessDefinitionKey());
        }
        if (ArrayUtil.isNotEmpty(pageVO.getCreateTime())) {
            taskQuery.taskCreatedAfter(DateUtils.of(pageVO.getCreateTime()[0]));
            taskQuery.taskCreatedBefore(DateUtils.of(pageVO.getCreateTime()[1]));
        }
        long count = taskQuery.count();
        int startData = (pageVO.getStartPage() - 1) *pageVO.getPageSize();
        List<Task> tasks = taskQuery.listPage(startData, pageVO.getPageSize());
        return PageConverterUtils.convert(tasks,pageVO.getStartPage(),pageVO.getPageSize(),count);
    }

    /**
     * 获得用户（待办）的任务：
     * 1. 根据 taskId 查询待办任务
     * 2. 如果任务不存在（或者已审核），获取指定流程下，首个需要处理任务
     *
     * @param userId 用户编号
     * @param taskId 任务编号
     * @param processInstanceId 流程实例编号
     * @return 待办任务
     */
    @Override
    public BpmTaskDTO getTodoTask(Long userId, String taskId, String processInstanceId) {
        // 1.1 获取指定的用户待办任务
        Task todoTask = getMyTodoTask(userId, taskId);
        // 1.2 获取不到，则获取该流程实例下，第一个用户的待办任务
        if (todoTask == null) {
            todoTask = getMyFirstTodoTask(userId, processInstanceId);
        }
        if (todoTask == null) {
            return null;
        }

        // 2. 查询该任务的子任务
        List<Task> childrenTasks = getAllChildrenTaskListByParentTaskId(todoTask.getId(), CollUtil.newArrayList(todoTask));

        // 3. 转换返回
        BpmnModel bpmnModel = bpmProcessDefinitionService.getProcessDefinitionBpmnModel(todoTask.getProcessDefinitionId());
        Map<Integer, BpmTaskDTO.OperationButtonSetting> buttonsSetting = BpmnModelUtils.parseButtonsSetting(
                bpmnModel, todoTask.getTaskDefinitionKey());
        Boolean signEnable = parseSignEnable(bpmnModel, todoTask.getTaskDefinitionKey());
        Boolean reasonRequire = parseReasonRequire(bpmnModel, todoTask.getTaskDefinitionKey());
        Integer nodeType = parseNodeType(BpmnModelUtils.getFlowElementById(bpmnModel, todoTask.getTaskDefinitionKey()));

        // 4. 任务表单
        BpmFormPO taskForm = null;
        if (StrUtil.isNotBlank(todoTask.getFormKey())) {
            taskForm = formService.getDetail(NumberUtils.parseLong(todoTask.getFormKey()));
        }

        return BpmTaskConvert.INSTANCE.buildTodoTask(todoTask, childrenTasks, buttonsSetting, taskForm)
                .setNodeType(nodeType).setSignEnable(signEnable).setReasonRequire(reasonRequire);
    }

    /**
     * 获得用户指定 taskId 任务编号的“待办”（未审批、且可审核）的任务
     *
     * @param userId 用户编号
     * @param taskId 任务编号
     * @return 任务
     */
    private Task getMyTodoTask(Long userId, String taskId) {
        if (StrUtil.isEmpty(taskId)) {
            return null;
        }
        Task task = getTask(taskId);
        if (task == null) {
            return null;
        }
        if (!isAssignUserTask(userId, task) && !isAddSignUserTask(userId, task)) {
            return null;
        }
        return task;
    }

    /**
     * 获得用户指定 processInstanceId 流程编号下的首个“待办”（未审批、且可审核）的任务
     *
     * @param userId            用户编号
     * @param processInstanceId 流程编号
     * @return 任务
     */
    private Task getMyFirstTodoTask(Long userId, String processInstanceId) {
        if (processInstanceId == null) {
            return null;
        }
        // 1. 查询所有任务
        List<Task> tasks = taskService.createTaskQuery()
                .active()
                .processInstanceId(processInstanceId)
                .includeTaskLocalVariables()
                .includeProcessVariables()
                .orderByTaskCreateTime().asc() // 按创建时间升序
                .list();

        // 2. 查询我的首个任务
        return CollUtil.findOne(tasks, task -> {
            return isAssignUserTask(userId, task) // 当前用户为审批人
                    || isAddSignUserTask(userId, task); // 当前用户为加签人（为了减签）
        });
    }

    /**
     * 获得已办的流程任务分页
     *
     * @param userId    用户编号
     * @param pageVO 分页请求
     * @return 流程任务分页
     */
    @Override
    public Pages<HistoricTaskInstance> getTaskDonePage(Long userId, BpmTaskSearchDTO pageVO) {
        HistoricTaskInstanceQuery taskQuery = historyService.createHistoricTaskInstanceQuery()
                .finished() // 已完成
                .taskAssignee(String.valueOf(userId)) // 分配给自己
                .includeTaskLocalVariables()
                .orderByHistoricTaskInstanceEndTime().desc(); // 审批时间倒序
        if (StrUtil.isNotBlank(pageVO.getName())) {
            taskQuery.taskNameLike("%" + pageVO.getName() + "%");
        }
        if (StrUtil.isNotBlank(pageVO.getProcessDefinitionName())) {
            taskQuery.processDefinitionNameLike("%" + pageVO.getProcessDefinitionName() + "%");
        }
        if (StrUtil.isNotEmpty(pageVO.getCategory())) {
            taskQuery.taskCategory(pageVO.getCategory());
        }
        if (pageVO.getStatus() != null) {
            taskQuery.taskVariableValueEquals(BpmnVariableConstants.TASK_VARIABLE_STATUS, pageVO.getStatus());
        }
//        if (ArrayUtil.isNotEmpty(pageVO.getCreateTime())) {
//            taskQuery.taskCreatedAfter(DateUtils.of(pageVO.getCreateTime()[0]));
//            taskQuery.taskCreatedBefore(DateUtils.of(pageVO.getCreateTime()[1]));
//        }
        // 执行查询
        long count = taskQuery.count();
        int startData = (pageVO.getStartPage()-1)*pageVO.getPageSize();

        List<HistoricTaskInstance> tasks = taskQuery.listPage(startData, pageVO.getPageSize());

        // 特殊：强制移除自动完成的“发起人”节点
        // 补充说明：由于 taskQuery 无法方面的过滤，所以暂时通过内存过滤
        tasks.removeIf(task -> task.getTaskDefinitionKey().equals(START_USER_NODE_ID));
        // TODO @芋艿：https://t.zsxq.com/MNzqp 【flowable bug】：taskCreatedAfter、taskCreatedBefore 拼接的是 OR
        if (ArrayUtil.isNotEmpty(pageVO.getCreateTime())) {
            tasks.removeIf(task -> task.getCreateTime() == null
                    || task.getCreateTime().before(DateUtils.of(pageVO.getCreateTime()[0]))
                    || task.getCreateTime().after(DateUtils.of(pageVO.getCreateTime()[1])));
        }
        return PageConverterUtils.convert(tasks,pageVO.getStartPage(),pageVO.getPageSize(),count);
    }

    /**
     * 获得全部的流程任务分页
     *
     * @param userId    用户编号
     * @param pageVO 分页请求
     * @return 流程任务分页
     */
    @Override
    public Pages<HistoricTaskInstance> getTaskPage(Long userId, BpmTaskSearchDTO pageVO) {
        HistoricTaskInstanceQuery taskQuery = historyService.createHistoricTaskInstanceQuery()
                .includeTaskLocalVariables()
//                .taskTenantId(FlowableUtils.getTenantId())
                .orderByHistoricTaskInstanceEndTime().desc(); // 审批时间倒序
        if (StrUtil.isNotBlank(pageVO.getName())) {
            taskQuery.taskNameLike("%" + pageVO.getName() + "%");
        }
        if (StrUtil.isNotBlank(pageVO.getProcessDefinitionName())) {
            taskQuery.processDefinitionNameLike("%" + pageVO.getProcessDefinitionName() + "%");
        }
        if (StrUtil.isNotEmpty(pageVO.getCategory())) {
            taskQuery.taskCategory(pageVO.getCategory());
        }
//        if (ArrayUtil.isNotEmpty(pageVO.getCreateTime())) {
//            taskQuery.taskCreatedAfter(DateUtils.of(pageVO.getCreateTime()[0]));
//            taskQuery.taskCreatedBefore(DateUtils.of(pageVO.getCreateTime()[1]));
//        }
        // 执行查询
        long count = taskQuery.count();

        int startData = (pageVO.getStartPage()-1)*pageVO.getPageSize();
        List<HistoricTaskInstance> tasks = taskQuery.listPage(startData, pageVO.getPageSize());
        // TODO @芋艿：https://t.zsxq.com/MNzqp 【flowable bug】：taskCreatedAfter、taskCreatedBefore 拼接的是 OR
        if (ArrayUtil.isNotEmpty(pageVO.getCreateTime())) {
            tasks.removeIf(task -> task.getCreateTime() == null
                    || task.getCreateTime().before(DateUtils.of(pageVO.getCreateTime()[0]))
                    || task.getCreateTime().after(DateUtils.of(pageVO.getCreateTime()[1])));
        }
        return PageConverterUtils.convert(tasks,pageVO.getStartPage(),pageVO.getPageSize(),count);
    }

    /**
     * 获得流程任务列表
     *
     * @param processInstanceIds 流程实例的编号数组
     * @return 流程任务列表
     */
    @Override
    public List<Task> getTasksByProcessInstanceIds(List<String> processInstanceIds) {
        if (CollUtil.isEmpty(processInstanceIds)) {
            return Collections.emptyList();
        }
        return taskService.createTaskQuery().processInstanceIdIn(processInstanceIds).list();
    }

    /**
     * 获得指定流程实例的流程任务列表，包括所有状态的
     *
     * @param processInstanceId 流程实例的编号
     * @param asc               是否升序
     * @return 流程任务列表
     */
    @Override
    public List<HistoricTaskInstance> getTaskListByProcessInstanceId(String processInstanceId, Boolean asc) {
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
                .includeTaskLocalVariables()
                .processInstanceId(processInstanceId);
        if (Boolean.TRUE.equals(asc)) {
            query.orderByHistoricTaskInstanceStartTime().asc();
        } else {
            query.orderByHistoricTaskInstanceStartTime().desc();
        }
        return query.list();
    }

    @Override
    public Task validateTask(Long userId, String taskId) {
        Task task = validateTaskExist(taskId);
        // 为什么判断 assignee 非空的情况下？
        // 例如说：在审批人为空时，我们会有“自动审批通过”的策略，此时 userId 为 null，允许通过
        if (StrUtil.isNotBlank(task.getAssignee())
                && ObjectUtil.notEqual(userId, NumberUtils.parseLong(task.getAssignee()))) {
            throw exception(TASK_OPERATE_FAIL_ASSIGN_NOT_SELF);
        }
        return task;
    }

    private Task validateTaskExist(String id) {
        Task task = getTask(id);
        if (task == null) {
            throw exception(TASK_NOT_EXISTS);
        }
        return task;
    }

    /**
     * 获取任务
     *
     * @param id 任务编号
     * @return 任务
     */
    @Override
    public Task getTask(String id) {
        return taskService.createTaskQuery().taskId(id).includeTaskLocalVariables().singleResult();
    }

    /**
     * 获取历史任务
     *
     * @param id 任务编号
     * @return 历史任务
     */
    @Override
    public HistoricTaskInstance getHistoricTask(String id) {
        return historyService.createHistoricTaskInstanceQuery().taskId(id).includeTaskLocalVariables().singleResult();
    }

    /**
     * 获取历史任务列表
     *
     * @param taskIds 任务编号集合
     * @return 历史任务列表
     */
    @Override
    public List<HistoricTaskInstance> getHistoricTasks(Collection<String> taskIds) {
        return historyService.createHistoricTaskInstanceQuery().taskIds(taskIds).includeTaskLocalVariables().list();
    }

    /**
     * 根据条件查询正在进行中的任务
     *
     * @param processInstanceId 流程实例编号，不允许为空
     * @param assigned          是否分配了审批人，允许空
     * @param defineKey     任务定义 Key，允许空
     */
    @Override
    public List<Task> getRunningTaskListByProcessInstanceId(String processInstanceId, Boolean assigned, String defineKey) {
        Assert.notNull(processInstanceId, "processInstanceId 不能为空");
        TaskQuery taskQuery = taskService.createTaskQuery().processInstanceId(processInstanceId).active()
                .includeTaskLocalVariables();
        if (BooleanUtil.isTrue(assigned)) {
            taskQuery.taskAssigned();
        } else if (BooleanUtil.isFalse(assigned)) {
            taskQuery.taskUnassigned();
        }
        if (StrUtil.isNotEmpty(defineKey)) {
            taskQuery.taskDefinitionKey(defineKey);
        }
        return taskQuery.list();
    }

    /**
     * 获取当前任务的可退回的 UserTask 集合
     *
     * @param id 当前的任务 ID
     * @return 可以退回的节点列表
     */
    @Override
    public List<UserTask> getUserTaskListByReturn(String id) {
        // 1.1 校验当前任务 task 存在
        Task task = validateTaskExist(id);
        // 1.2 根据流程定义获取流程模型信息
        BpmnModel bpmnModel = modelService.getBpmnModelByDefinitionId(task.getProcessDefinitionId());
        FlowElement source = BpmnModelUtils.getFlowElementById(bpmnModel, task.getTaskDefinitionKey());
        if (source == null) {
            throw exception(TASK_NOT_EXISTS);
        }

        // 2.1 查询该任务的前置任务节点的 key 集合
        List<UserTask> previousUserList = BpmnModelUtils.getPreviousUserTaskList(source, null, null);
        if (CollUtil.isEmpty(previousUserList)) {
            return Collections.emptyList();
        }
        // 2.2 过滤：只有串行可到达的节点，才可以退回。类似非串行、子流程无法退回
        previousUserList.removeIf(userTask -> !BpmnModelUtils.isSequentialReachable(source, userTask, null));

        // 2.3 过滤：只能退回到已经处理过的节点（排除审批未经过的节点）。相关 issue：https://github.com/YunaiV/ruoyi-vue-pro/issues/982
        List<HistoricTaskInstance> finishedTasks = getFinishedTaskListByProcessInstanceIdWithoutCancel(task.getProcessInstanceId());
        Set<String> finishedTaskDefinitionKeys = convertSet(finishedTasks, HistoricTaskInstance::getTaskDefinitionKey);
        previousUserList.removeIf(userTask -> !finishedTaskDefinitionKeys.contains(userTask.getId()));
        return previousUserList;
    }

    /**
     * 获取指定任务的子任务列表（多层）
     *
     * @param parentTaskId 父任务 ID
     * @param tasks 任务列表
     * @return 子任务列表
     */
    @Override
    public <T extends TaskInfo> List<T> getAllChildrenTaskListByParentTaskId(String parentTaskId, List<T> tasks) {
        if (CollUtil.isEmpty(tasks)) {
            return Collections.emptyList();
        }
        Map<String, List<T>> parentTaskMap = convertMultiMap(
                filterList(tasks, task -> StrUtil.isNotEmpty(task.getParentTaskId())), TaskInfo::getParentTaskId);
        if (CollUtil.isEmpty(parentTaskMap)) {
            return Collections.emptyList();
        }

        List<T> result = new ArrayList<>();
        // 1. 递归获取子级
        Stack<String> stack = new Stack<>();
        stack.push(parentTaskId);
        // 2. 递归遍历
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            if (stack.isEmpty()) {
                break;
            }
            // 2.1 获取子任务们
            String taskId = stack.pop();
            List<T> childTaskList = filterList(tasks, task -> StrUtil.equals(task.getParentTaskId(), taskId));
            // 2.2 如果非空，则添加到 stack 进一步递归
            if (CollUtil.isNotEmpty(childTaskList)) {
                stack.addAll(convertList(childTaskList, TaskInfo::getId));
                result.addAll(childTaskList);
            }
        }
        return result;
    }

    /**
     * 获得所有子任务列表
     *
     * @param parentTask 父任务
     * @return 所有子任务列表
     */
    private List<Task> getAllChildTaskList(Task parentTask) {
        List<Task> result = new ArrayList<>();
        // 1. 递归获取子级
        Stack<Task> stack = new Stack<>();
        stack.push(parentTask);
        // 2. 递归遍历
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            if (stack.isEmpty()) {
                break;
            }
            // 2.1 获取子任务们
            Task task = stack.pop();
            List<Task> childTaskList = getTaskListByParentTaskId(task.getId());
            // 2.2 如果非空，则添加到 stack 进一步递归
            if (CollUtil.isNotEmpty(childTaskList)) {
                stack.addAll(childTaskList);
                result.addAll(childTaskList);
            }
        }
        return result;
    }

    @Override
    public List<Task> getTaskListByParentTaskId(String parentTaskId) {
        String tableName = managementService.getTableName(TaskEntity.class);
        // taskService.createTaskQuery() 没有 parentId 参数，所以写 sql 查询
        String sql = "select ID_,NAME_,OWNER_,ASSIGNEE_ from " + tableName + " where PARENT_TASK_ID_=#{parentTaskId}";
        return taskService.createNativeTaskQuery().sql(sql).parameter("parentTaskId", parentTaskId).list();
    }

    /**
     * 获取子任务个数
     *
     * @param parentTaskId 父任务 ID
     * @return 剩余子任务个数
     */
    private Long getTaskCountByParentTaskId(String parentTaskId) {
        String tableName = managementService.getTableName(TaskEntity.class);
        String sql = "SELECT COUNT(1) from " + tableName + " WHERE PARENT_TASK_ID_=#{parentTaskId}";
        return taskService.createNativeTaskQuery().sql(sql).parameter("parentTaskId", parentTaskId).count();
    }

    /**
     * 获得任务根任务的父任务编号
     *
     * @param task 任务
     * @return 根任务的父任务编号
     */
    private String getTaskRootParentId(Task task) {
        if (task == null || task.getParentTaskId() == null) {
            return null;
        }
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            Task parentTask = getTask(task.getParentTaskId());
            if (parentTask == null) {
                return null;
            }
            if (parentTask.getParentTaskId() == null) {
                return parentTask.getId();
            }
            task = parentTask;
        }
        throw new IllegalArgumentException(String.format("Task(%s) 层级过深，无法获取父节点编号", task.getId()));
    }


    /**
     * 获得指定流程实例的活动实例列表
     *
     * @param processInstanceId 流程实例的编号
     * @return 活动实例列表
     */
    @Override
    public List<HistoricActivityInstance> getActivityListByProcessInstanceId(String processInstanceId) {
        return historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime().asc().list();
    }

    /**
     * 获得执行编号对应的活动实例
     *
     * @param executionId 执行编号
     * @return 活动实例
     */
    @Override
    public List<HistoricActivityInstance> getHistoricActivityListByExecutionId(String executionId) {
        return historyService.createHistoricActivityInstanceQuery().executionId(executionId).list();
    }

    /**
     * 获得指定流程实例的已完成的流程任务列表，不包含取消状态
     *
     * @param processInstanceId 流程实例的编号
     * @return 流程任务列表
     */
    @Override
    public List<HistoricTaskInstance> getFinishedTaskListByProcessInstanceIdWithoutCancel(String processInstanceId) {
        return historyService.createHistoricTaskInstanceQuery()
                .finished()
                .includeTaskLocalVariables()
                .processInstanceId(processInstanceId)
                .taskVariableValueNotEquals(BpmnVariableConstants.TASK_VARIABLE_STATUS,
                        BpmTaskStatusEnum.CANCEL.getStatus())
                .orderByHistoricTaskInstanceStartTime().asc().list();
    }

    /**
     * 判断指定用户，是否是当前任务的审批人
     *
     * @param userId 用户编号
     * @param task   任务
     * @return 是否
     */
    private boolean isAssignUserTask(Long userId, Task task) {
        Long assignee = NumberUtil.parseLong(task.getAssignee(), null);
        return ObjectUtil.equals(userId, assignee);
    }

    /**
     * 判断指定用户，是否是当前任务的拥有人
     *
     * @param userId 用户编号
     * @param task   任务
     * @return 是否
     */
    private boolean isOwnerUserTask(Long userId, Task task) {
        Long assignee = NumberUtil.parseLong(task.getOwner(), null);
        return ObjectUtil.equal(userId, assignee);
    }

    /**
     * 判断指定用户，是否是当前任务的加签人
     *
     * @param userId 用户 Id
     * @param task   任务
     * @return 是否
     */
    private boolean isAddSignUserTask(Long userId, Task task) {
        return (isAssignUserTask(userId, task) || isOwnerUserTask(userId, task))
                && BpmTaskSignTypeEnum.of(task.getScopeType()) != null;
    }

    // ========== Update 写入相关方法 ==========

    /**
     * 通过任务
     *
     * @param userId 用户编号
     * @param reqVO  通过请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveTask(Long userId, BpmTaskApproveDTO reqVO) {
        // 1.1 校验任务存在
        Task task = validateTask(userId, reqVO.getId());
        // 1.2 校验流程实例存在
        ProcessInstance instance = processInstanceService.getProcessInstance(task.getProcessInstanceId());
        if (instance == null) {
            throw exception(PROCESS_INSTANCE_NOT_EXISTS);
        }
        // 1.3 校验签名
        BpmnModel bpmnModel = modelService.getBpmnModelByDefinitionId(task.getProcessDefinitionId());
        Boolean signEnable = parseSignEnable(bpmnModel, task.getTaskDefinitionKey());
        if (signEnable && StrUtil.isEmpty(reqVO.getSignPicUrl())) {
            throw exception(TASK_SIGNATURE_NOT_EXISTS);
        }
        // 1.4 校验审批意见
        Boolean reasonRequire = parseReasonRequire(bpmnModel, task.getTaskDefinitionKey());
        if (reasonRequire && StrUtil.isEmpty(reqVO.getReason())) {
            throw exception(TASK_REASON_REQUIRE);
        }

        // 情况一：被委派的任务，不调用 complete 去完成任务
        if (DelegationState.PENDING.equals(task.getDelegationState())) {
            approveDelegateTask(reqVO, task);
            return;
        }

        // 情况二：审批有【后】加签的任务
        if (BpmTaskSignTypeEnum.AFTER.getType().equals(task.getScopeType())) {
            approveAfterSignTask(task, reqVO);
            return;
        }

        // 情况三：审批普通的任务。大多数情况下，都是这样
        // 2.1 更新 task 状态、原因、签字
        updateTaskStatusAndReason(task.getId(), BpmTaskStatusEnum.APPROVE.getStatus(), reqVO.getReason());
        if (signEnable) {
            taskService.setVariableLocal(task.getId(), BpmnVariableConstants.TASK_SIGN_PIC_URL, reqVO.getSignPicUrl());
            String businessId = task.getId();
            sysFileService.saveFileBusRelation(reqVO.getFileIds(),businessId);
        }
        // 2.2 添加评论
        taskService.addComment(task.getId(), task.getProcessInstanceId(), BpmCommentTypeEnum.APPROVE.getType(),
                BpmCommentTypeEnum.APPROVE.formatComment(reqVO.getReason()));

        // 3. 设置流程变量。如果流程变量前端传空，需要从历史实例中获取，原因：前端表单如果在当前节点无可编辑的字段时 variables 一定会为空
        // 场景一：A 节点发起，B 节点表单无可编辑字段，审批通过时，C 节点需要流程变量获取下一个执行节点，但因为 B 节点无可编辑的字段，variables 为空，流程可能出现问题。
        // 场景二：A 节点发起，B 节点只有某一个字段可编辑（比如 day），但 C 节点需要多个节点。
        //       （比如 work + day 变量，在发起时填写，因为 B 节点只有 day 的编辑权限，在审批后，variables 会缺少 work 的值）
        Map<String, Object> processVariables = new HashMap<>();
        if (CollUtil.isNotEmpty(instance.getProcessVariables())) { // 获取历史中流程变量
            processVariables.putAll(instance.getProcessVariables());
        }
        if (CollUtil.isNotEmpty(reqVO.getVariables())) { // 合并前端传递的流程变量，以前端为准
            processVariables.putAll(reqVO.getVariables());
        }

        // 4. 校验并处理 APPROVE_USER_SELECT 当前审批人，选择下一节点审批人的逻辑
        Map<String, Object> variables = validateAndSetNextAssignees(task.getTaskDefinitionKey(), processVariables,
                bpmnModel, reqVO.getNextAssignees(), instance);
        runtimeService.setVariables(task.getProcessInstanceId(), variables);

        // 5. 如果当前节点 Id 存在于需要预测的流程节点中，从中移除。 流程变量在回退操作中设置
        Object needSimulateTaskIds = runtimeService.getVariable(task.getProcessInstanceId(), BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_NEED_SIMULATE_TASK_IDS);
        Set<String> needSimulateTaskIdsByReturn = Convert.toSet(String.class, needSimulateTaskIds);
        if (needSimulateTaskIdsByReturn.contains(task.getTaskDefinitionKey())) {
            needSimulateTaskIdsByReturn.remove(task.getTaskDefinitionKey());
            runtimeService.setVariable(task.getProcessInstanceId(), BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_NEED_SIMULATE_TASK_IDS, needSimulateTaskIdsByReturn);
        }

        // 6. 调用 BPM complete 去完成任务
        taskService.complete(task.getId(), variables, true);

        // 【加签专属】处理加签任务
        handleParentTaskIfSign(task.getParentTaskId());

        // 推送【审批通过】消息给发起人
        try {
            // 找出流程发起人
            SysUserDTO startUser = sysUserService.getById(NumberUtils.parseLong(instance.getStartUserId()));

            // 找出当前审批人姓名
            SysUserDTO currentUser = sysUserService.getById(userId);
            String operatorName = currentUser != null ? currentUser.getUserName() : "系统";

            // 获取流程名称
            String processName = StrUtil.isNotBlank(instance.getName())
                    ? instance.getName() : "业务单据";

            if (startUser != null && StrUtil.isNotBlank(startUser.getUserAccount())) {
                WebSocketUtils.sendTemplateNotification(
                        startUser.getUserAccount(),
                        BpmMessageConstants.TASK_COMPLETED,
                        operatorName,
                        processName,
                        task.getName()
                );
            }
        } catch (Exception e) {
            log.error("[WebSocket推送] 审批通过通知发送失败, taskId: {}", task.getId(), e);
        }
    }

    /**
     * 校验选择的下一个节点的审批人，是否合法
     * <p>
     * 1. 是否有漏选：没有选择审批人
     * 2. 是否有多选：非下一个节点
     *
     * @param taskDefinitionKey 当前任务节点标识
     * @param variables         流程变量
     * @param bpmnModel         流程模型
     * @param nextAssignees     下一个节点审批人集合（参数）
     * @param processInstance   流程实例
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> validateAndSetNextAssignees(String taskDefinitionKey, Map<String, Object> variables, BpmnModel bpmnModel,
                                                            Map<String, List<Long>> nextAssignees, ProcessInstance processInstance) {
        // simple 设计器第一个节点默认为发起人节点，不校验是否存在审批人
        if (Objects.equals(taskDefinitionKey, START_USER_NODE_ID)) {
            return variables;
        }
        // 1. 获取下一个将要执行的节点集合
        FlowElement flowElement = bpmnModel.getFlowElement(taskDefinitionKey);
        List<FlowNode> nextFlowNodes = getNextFlowNodes(flowElement, bpmnModel, variables);

        // 2. 校验选择的下一个节点的审批人，是否合法
        for (FlowNode nextFlowNode : nextFlowNodes) {
            Integer candidateStrategy = parseCandidateStrategy(nextFlowNode);
            // 2.1 情况一：如果节点中的审批人策略为 发起人自选
            if (ObjUtil.equals(candidateStrategy, BpmTaskCandidateStrategyEnum.START_USER_SELECT.getStrategy())) {
                // 特殊：如果当前节点已经存在审批人，则不允许覆盖
                Map<String, List<Long>> startUserSelectAssignees = FlowableUtils.getStartUserSelectAssignees(processInstance.getProcessVariables());
                if (startUserSelectAssignees != null && CollUtil.isNotEmpty(startUserSelectAssignees.get(nextFlowNode.getId()))) {
                    continue;
                }
                // 如果节点存在，但未配置审批人
                List<Long> assignees = nextAssignees != null ? nextAssignees.get(nextFlowNode.getId()) : null;
                if (CollUtil.isEmpty(assignees)) {
                    throw exception(PROCESS_INSTANCE_START_USER_SELECT_ASSIGNEES_NOT_CONFIG, nextFlowNode.getName());
                }

                // 设置 PROCESS_INSTANCE_VARIABLE_START_USER_SELECT_ASSIGNEES
                if (startUserSelectAssignees == null) {
                    startUserSelectAssignees = new HashMap<>();
                }
                startUserSelectAssignees.put(nextFlowNode.getId(), assignees);
                variables.put(BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_START_USER_SELECT_ASSIGNEES, startUserSelectAssignees);
                continue;
            }

            // 2.2 情况二：如果节点中的审批人策略为 审批人，在审批时选择下一个节点的审批人，并且该节点的审批人为空
            if (ObjUtil.equals(candidateStrategy, BpmTaskCandidateStrategyEnum.APPROVE_USER_SELECT.getStrategy())) {
                // 如果节点存在，但未配置审批人
                Map<String, List<Long>> approveUserSelectAssignees = FlowableUtils.getApproveUserSelectAssignees(processInstance.getProcessVariables());
                List<Long> assignees = nextAssignees != null ? nextAssignees.get(nextFlowNode.getId()) : null;
                if (CollUtil.isEmpty(assignees)) {
                    throw exception(PROCESS_INSTANCE_APPROVE_USER_SELECT_ASSIGNEES_NOT_CONFIG, nextFlowNode.getName());
                }

                // 设置 PROCESS_INSTANCE_VARIABLE_APPROVE_USER_SELECT_ASSIGNEES
                if (approveUserSelectAssignees == null) {
                    approveUserSelectAssignees = new HashMap<>();
                }
                approveUserSelectAssignees.put(nextFlowNode.getId(), assignees);
                Map<String, List<Long>> existingApproveUserSelectAssignees = (Map<String, List<Long>>) variables.get(
                        BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_APPROVE_USER_SELECT_ASSIGNEES);
                if (CollUtil.isNotEmpty(existingApproveUserSelectAssignees)) {
                    approveUserSelectAssignees.putAll(existingApproveUserSelectAssignees);
                }
                variables.put(BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_APPROVE_USER_SELECT_ASSIGNEES, approveUserSelectAssignees);
            }
        }
        return variables;
    }

    /**
     * 审批通过存在“后加签”的任务。
     * <p>
     * 注意：该任务不能马上完成，需要一个中间状态（APPROVING），并激活剩余所有子任务（PROCESS）为可审批处理
     * 如果马上完成，则会触发下一个任务，甚至如果没有下一个任务则流程实例就直接结束了！
     *
     * @param task  当前任务
     * @param reqVO 前端请求参数
     */
    private void approveAfterSignTask(Task task, BpmTaskApproveDTO reqVO) {
        // 更新父 task 状态 + 原因
        updateTaskStatusAndReason(task.getId(), BpmTaskStatusEnum.APPROVING.getStatus(), reqVO.getReason());

        // 2. 激活子任务
        List<Task> childrenTaskList = getTaskListByParentTaskId(task.getId());
        for (Task childrenTask : childrenTaskList) {
            taskService.resolveTask(childrenTask.getId());
            // 更新子 task 状态
            updateTaskStatus(childrenTask.getId(), BpmTaskStatusEnum.RUNNING.getStatus());
        }
    }

    /**
     * 如果父任务是有前后【加签】的任务，如果它【加签】出来的子任务都被处理，需要处理父任务：
     * <p>
     * 1. 如果是【向前】加签，则需要重新激活父任务，让它可以被审批
     * 2. 如果是【向后】加签，则需要完成父任务，让它完成审批
     *
     * @param parentTaskId 父任务编号
     */
    private void handleParentTaskIfSign(String parentTaskId) {
        if (StrUtil.isBlank(parentTaskId)) {
            return;
        }
        // 1.1 判断是否还有子任务。如果没有，就不处理
        Long childrenTaskCount = getTaskCountByParentTaskId(parentTaskId);
        if (childrenTaskCount > 0) {
            return;
        }
        // 1.2 只处理加签的父任务
        Task parentTask = validateTaskExist(parentTaskId);
        String scopeType = parentTask.getScopeType();
        if (BpmTaskSignTypeEnum.of(scopeType) == null) {
            return;
        }

        // 2. 子任务已处理完成，清空 scopeType 字段，修改 parentTask 信息，方便后续可以继续向前后向后加签
        TaskEntityImpl parentTaskImpl = (TaskEntityImpl) parentTask;
        parentTaskImpl.setScopeType(null);
        taskService.saveTask(parentTaskImpl);

        // 3.1 情况一：处理向【向前】加签
        if (BpmTaskSignTypeEnum.BEFORE.getType().equals(scopeType)) {
            // 3.1.1 owner 重新赋值给父任务的 assignee，这样它就可以被审批
            taskService.resolveTask(parentTaskId);
            // 3.1.2 更新流程任务 status
            updateTaskStatus(parentTaskId, BpmTaskStatusEnum.RUNNING.getStatus());
            // 3.2 情况二：处理向【向后】加签
        } else if (BpmTaskSignTypeEnum.AFTER.getType().equals(scopeType)) {
            // 只有 parentTask 处于 APPROVING 的情况下，才可以继续 complete 完成
            // 否则，一个未审批的 parentTask 任务，在加签出来的任务都被减签的情况下，就直接完成审批，这样会存在问题
            Integer status = (Integer) parentTask.getTaskLocalVariables().get(BpmnVariableConstants.TASK_VARIABLE_STATUS);
            if (ObjectUtil.notEqual(status, BpmTaskStatusEnum.APPROVING.getStatus())) {
                return;
            }
            // 3.2.2 完成自己（因为它已经没有子任务，所以也可以完成）
            updateTaskStatus(parentTaskId, BpmTaskStatusEnum.APPROVE.getStatus());
            taskService.complete(parentTaskId);
        }

        // 4. 递归处理父任务
        handleParentTaskIfSign(parentTask.getParentTaskId());
    }

    /**
     * 审批被委派的任务
     *
     * @param reqVO 前端请求参数，包含当前任务ID，审批意见等
     * @param task  当前被审批的任务
     */
    private void approveDelegateTask(BpmTaskApproveDTO reqVO, Task task) {
        // 1. 添加审批意见
        SysUserDTO currentUser = sysUserService.getById(SecurityUtils.getLoginUserId());
        SysUserDTO ownerUser = sysUserService.getById(NumberUtils.parseLong(task.getOwner())); // 发起委托的用户
        Assert.notNull(ownerUser, "委派任务找不到原审批人，需要检查数据");
        taskService.addComment(reqVO.getId(), task.getProcessInstanceId(), BpmCommentTypeEnum.DELEGATE_END.getType(),
                BpmCommentTypeEnum.DELEGATE_END.formatComment(currentUser.getUserName(), ownerUser.getUserName(), reqVO.getReason()));

        // 2.1 调用 resolveTask 完成任务。
        // 底层调用 TaskHelper.changeTaskAssignee(task, task.getOwner())：将 owner 设置为 assignee
        taskService.resolveTask(task.getId());
        // 2.2 更新 task 状态 + 原因
        updateTaskStatusAndReason(task.getId(), BpmTaskStatusEnum.RUNNING.getStatus(), reqVO.getReason());
    }

    /**
     * 不通过任务
     *
     * @param userId 用户编号
     * @param reqVO  不通过请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectTask(Long userId, @Valid BpmTaskRejectDTO reqVO) {
        // 1.1 校验任务存在
        Task task = validateTask(userId, reqVO.getId());
        // 1.2 校验流程实例存在
        ProcessInstance instance = processInstanceService.getProcessInstance(task.getProcessInstanceId());
        if (instance == null) {
            throw exception(PROCESS_INSTANCE_NOT_EXISTS);
        }

        // 2.1 更新流程任务为不通过
        updateTaskStatusAndReason(task.getId(), BpmTaskStatusEnum.REJECT.getStatus(), reqVO.getReason());
        // 2.2 添加流程评论
        taskService.addComment(task.getId(), task.getProcessInstanceId(), BpmCommentTypeEnum.REJECT.getType(),
                BpmCommentTypeEnum.REJECT.formatComment(reqVO.getReason()));
        // 2.3 如果当前任务时被加签的，则加它的根任务也标记成未通过
        // 疑问：为什么要标记未通过呢？
        // 回答：例如说 A 任务被向前加签除 B 任务时，B 任务被审批不通过，此时 A 会被取消。而 yudao-ui-admin-vue3 不展示“已取消”的任务，导致展示不出审批不通过的细节。
        if (task.getParentTaskId() != null) {
            String rootParentId = getTaskRootParentId(task);
            updateTaskStatusAndReason(rootParentId, BpmTaskStatusEnum.REJECT.getStatus(),
                    BpmCommentTypeEnum.REJECT.formatComment("加签任务不通过"));
            taskService.addComment(rootParentId, task.getProcessInstanceId(), BpmCommentTypeEnum.REJECT.getType(),
                    BpmCommentTypeEnum.REJECT.formatComment("加签任务不通过"));
        }

        // 3. 根据不同的 RejectHandler 处理策略
        BpmnModel bpmnModel = modelService.getBpmnModelByDefinitionId(task.getProcessDefinitionId());
        FlowElement userTaskElement = BpmnModelUtils.getFlowElementById(bpmnModel, task.getTaskDefinitionKey());
        // 3.1 情况一：驳回到指定的任务节点
        BpmUserTaskRejectHandlerTypeEnum userTaskRejectHandlerType = BpmnModelUtils.parseRejectHandlerType(userTaskElement);
        if (userTaskRejectHandlerType == BpmUserTaskRejectHandlerTypeEnum.RETURN_USER_TASK) {
            String returnTaskId = BpmnModelUtils.parseReturnTaskId(userTaskElement);
            Assert.notNull(returnTaskId, "退回的节点不能为空");
            returnTask(userId, new BpmTaskReturnDTO().setId(task.getId())
                    .setTargetTaskDefinitionKey(returnTaskId).setReason(reqVO.getReason()));
            return;
        }

        // 3.2 情况二： 标记流程为不通过并结束流程
        processInstanceService.updateProcessInstanceReject(instance, reqVO.getReason()); // 标记不通过
        moveTaskToEnd(task.getProcessInstanceId(), BpmCommentTypeEnum.REJECT.formatComment(reqVO.getReason())); // 结束流程

        // 推送【驳回】消息给发起人
        try {
            SysUserDTO startUser = sysUserService.getById(NumberUtils.parseLong(instance.getStartUserId()));
            if (startUser != null && StrUtil.isNotBlank(startUser.getUserAccount())) {
                WebSocketUtils.sendTemplateNotification(
                        startUser.getUserAccount(),
                        BpmMessageConstants.TASK_REJECTED,
                        task.getName(),
                        StrUtil.blankToDefault(reqVO.getReason(), "无")
                );
            }
        } catch (Exception e) {
            log.error("[WebSocket推送] 驳回通知发送失败, taskId: {}", task.getId(), e);
        }
    }

    /**
     * 更新流程任务的 status 状态
     *
     * @param id     任务编号
     * @param status 状态
     */
    private void updateTaskStatus(String id, Integer status) {
        taskService.setVariableLocal(id, BpmnVariableConstants.TASK_VARIABLE_STATUS, status);
    }

    /**
     * 更新流程任务的 status 状态、reason 理由
     *
     * @param id     任务编号
     * @param status 状态
     * @param reason 理由（审批通过、审批不通过的理由）
     */
    private void updateTaskStatusAndReason(String id, Integer status, String reason) {
        updateTaskStatus(id, status);
        taskService.setVariableLocal(id, BpmnVariableConstants.TASK_VARIABLE_REASON, reason);
    }

    /**
     * 将任务退回到指定的 targetDefinitionKey 位置
     *
     * @param userId 用户编号
     * @param reqVO  退回的任务key和当前所在的任务ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnTask(Long userId, BpmTaskReturnDTO reqVO) {
        // 1.1 当前任务 task
        Task task = validateTask(userId, reqVO.getId());
        if (task.isSuspended()) {
            throw exception(TASK_IS_PENDING);
        }
        // 1.2 获取流程模型信息
        BpmnModel bpmnModel = modelService.getBpmnModelByDefinitionId(task.getProcessDefinitionId());
        // 1.3 校验源头和目标节点的关系，并返回目标元素
        FlowElement targetElement = validateTargetTaskCanReturn(bpmnModel, task.getTaskDefinitionKey(),
                reqVO.getTargetTaskDefinitionKey());

        // 2. 调用 Flowable 框架的退回逻辑
        returnTask(userId, bpmnModel, task, targetElement, reqVO);
    }

    /**
     * 退回流程节点时，校验目标任务节点是否可退回
     *
     * @param bpmnModel 流程模型
     * @param sourceKey 当前任务节点 Key
     * @param targetKey 目标任务节点 key
     * @return 目标任务节点元素
     */
    private FlowElement validateTargetTaskCanReturn(BpmnModel bpmnModel, String sourceKey, String targetKey) {
        // 1.1 获取当前任务节点元素
        FlowElement source = BpmnModelUtils.getFlowElementById(bpmnModel, sourceKey);
        // 1.2 获取跳转的节点元素
        FlowElement target = BpmnModelUtils.getFlowElementById(bpmnModel, targetKey);
        if (target == null) {
            throw exception(TASK_TARGET_NODE_NOT_EXISTS);
        }

        // 2. 只有串行可到达的节点，才可以退回。类似非串行、子流程无法退回
        if (!BpmnModelUtils.isSequentialReachable(source, target, null)) {
            throw exception(TASK_RETURN_FAIL_SOURCE_TARGET_ERROR);
        }
        return target;
    }

    /**
     * 执行退回逻辑
     *
     * @param userId        用户编号
     * @param bpmnModel     流程模型
     * @param currentTask   当前退回的任务
     * @param targetElement 需要退回到的目标任务
     * @param reqVO         前端参数封装
     */
    public void returnTask(Long userId, BpmnModel bpmnModel, Task currentTask, FlowElement targetElement, BpmTaskReturnDTO reqVO) {
        // 1. 获得所有需要回撤的任务 taskDefinitionKey，用于稍后的 moveActivityIdsToSingleActivityId 回撤
        // 1.1 获取所有正常进行的任务节点 Key
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(currentTask.getProcessInstanceId()).list();
        List<String> runTaskKeyList = convertList(taskList, Task::getTaskDefinitionKey);
        // 1.2 通过 targetElement 的出口连线，计算在 runTaskKeyList 有哪些 key 需要被撤回
        // 为什么不直接使用 runTaskKeyList 呢？因为可能存在多个审批分支，例如说：A -> B -> C 和 D -> F，而只要 C 撤回到 A，需要排除掉 F
        List<UserTask> returnUserTaskList = BpmnModelUtils.iteratorFindChildUserTasks(targetElement, runTaskKeyList, null, null);
        List<String> returnTaskKeyList = convertList(returnUserTaskList, UserTask::getId);

        // 2. 给当前要被退回的 task 数组，设置退回意见
        taskList.forEach(task -> {
            // 需要排除掉，不需要设置退回意见的任务
            if (!returnTaskKeyList.contains(task.getTaskDefinitionKey())) {
                return;
            }

            // 判断是否分配给自己任务，因为会签任务，一个节点会有多个任务
            if (isAssignUserTask(userId, task)) { // 情况一：自己的任务，进行 RETURN 标记
                // 2.1.1 添加评论
                taskService.addComment(task.getId(), currentTask.getProcessInstanceId(), BpmCommentTypeEnum.RETURN.getType(),
                        BpmCommentTypeEnum.RETURN.formatComment(reqVO.getReason()));
                // 2.1.2 更新 task 状态 + 原因
                updateTaskStatusAndReason(task.getId(), BpmTaskStatusEnum.RETURN.getStatus(), reqVO.getReason());
            } else { // 情况二：别人的任务，进行 CANCEL 标记
                processTaskCanceled(task.getId());
            }
        });

        // 3. 构建需要预测的任务流程变量
        Set<String> needSimulateTaskDefinitionKeys = getNeedSimulateTaskDefinitionKeys(bpmnModel, currentTask, targetElement);

        // 4. 执行驳回
        // ① 使用 moveExecutionsToSingleActivityId 替换 moveActivityIdsToSingleActivityId。原因：当多实例任务回退的时候有问题。
        //    相关 issue: https://github.com/flowable/flowable-engine/issues/3944
        // ② flowable 7.2.0 版本后，继续使用 moveActivityIdsToSingleActivityId 方法。原因：flowable 7.2.0 版本修复了该问题。
        //    相关 issue：https://github.com/YunaiV/ruoyi-vue-pro/issues/1018
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(currentTask.getProcessInstanceId())
                .moveActivityIdsToSingleActivityId(returnTaskKeyList, reqVO.getTargetTaskDefinitionKey())
                // 设置需要预测的任务 ids 的流程变量，用于辅助预测
                .processVariable(BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_NEED_SIMULATE_TASK_IDS, needSimulateTaskDefinitionKeys)
                // 设置流程变量（local）节点退回标记, 用于退回到节点，不执行 BpmUserTaskAssignStartUserHandlerTypeEnum 策略，导致自动通过
                .localVariable(reqVO.getTargetTaskDefinitionKey(),
                        String.format(BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_RETURN_FLAG, reqVO.getTargetTaskDefinitionKey()), Boolean.TRUE)
                .changeState();

        // 5. 发送【退回】消息给被退回的接收人
        try {
            // 5.1 获取操作人姓名（也就是当前执行退回操作的人）
            SysUserDTO currentUser = sysUserService.getById(userId);
            String operatorName = currentUser != null ? currentUser.getUserName() : "系统";

            // 5.2 确定接收人账号（找出目标节点历史的处理人）
            String receiverAccount = null;

            // 先去历史任务表里找最近一次处理过这个目标节点的人
            List<HistoricTaskInstance> targetHistoryTasks = historyService.createHistoricTaskInstanceQuery()
                    .processInstanceId(currentTask.getProcessInstanceId())
                    .taskDefinitionKey(reqVO.getTargetTaskDefinitionKey())
                    .finished()
                    .orderByHistoricTaskInstanceEndTime().desc()
                    .list();

            if (CollUtil.isNotEmpty(targetHistoryTasks)) {
                // 拿到最近一次处理该节点的人的 ID
                String targetAssigneeId = targetHistoryTasks.get(0).getAssignee();
                if (StrUtil.isNotBlank(targetAssigneeId)) {
                    SysUserDTO targetUser = sysUserService.getById(NumberUtils.parseLong(targetAssigneeId));
                    if (targetUser != null) {
                        receiverAccount = targetUser.getUserAccount();
                    }
                }
            } else if (targetElement.getId().equals(BpmnModelConstants.START_USER_NODE_ID)) {
                // 特殊情况：如果是直接退回给发起人，但历史表没记录实体Task，直接去查流程的发起人
                ProcessInstance pi = processInstanceService.getProcessInstance(currentTask.getProcessInstanceId());
                if (pi != null && StrUtil.isNotBlank(pi.getStartUserId())) {
                    SysUserDTO startUser = sysUserService.getById(NumberUtils.parseLong(pi.getStartUserId()));
                    if (startUser != null) {
                        receiverAccount = startUser.getUserAccount();
                    }
                }
            }

            // 5.3 执行 WebSocket 模板推送
            if (StrUtil.isNotBlank(receiverAccount)) {
                WebSocketUtils.sendTemplateNotification(
                        receiverAccount,
                        BpmMessageConstants.TASK_RETURN,
                        operatorName,
                        currentTask.getName(),
                        StrUtil.blankToDefault(reqVO.getReason(), "无")
                );
            }
        } catch (Exception e) {
            log.error("[WebSocket推送] 退回通知发送失败, taskId: {}", currentTask.getId(), e);
        }
    }

    private Set<String> getNeedSimulateTaskDefinitionKeys(BpmnModel bpmnModel, Task currentTask, FlowElement targetElement) {
        // 1. 获取需要预测的任务的 definition key。因为当前任务还没完成，也需要预测
        Set<String> taskDefinitionKeys = CollUtil.newHashSet(currentTask.getTaskDefinitionKey());

        // 2.1 获取已结束任务按时间倒序排序
        List<HistoricTaskInstance> endTaskList = CollectionUtils.filterList(
                getTaskListByProcessInstanceId(currentTask.getProcessInstanceId(), Boolean.FALSE),
                item -> item.getEndTime() != null);
        // 2.2 从结束任务中找到最近一个的目标任务
        HistoricTaskInstance targetTask = findFirst(endTaskList,
                item -> item.getTaskDefinitionKey().equals(targetElement.getId()));
        if (targetTask == null) {
            return taskDefinitionKeys;
        }
        // 2.3 遍历已结束的任务，找到在 targetTask 之后生成的任务，且串行可达的任务
        endTaskList.forEach(item -> {
            FlowElement element = getFlowElementById(bpmnModel, item.getTaskDefinitionKey());
            // 如果已结束的任务在回退目标节点之后生成，且串行可达，则加到需要预测节点中
            // TODO 串行可达的方法需要和判断可回退节点 validateTargetTaskCanReturn 分开吗？ 并行网关可能会有问题。
            if (item.getCreateTime().compareTo(targetTask.getCreateTime()) > 0
                    && BpmnModelUtils.isSequentialReachable(element, targetElement, null)) {
                taskDefinitionKeys.add(item.getTaskDefinitionKey());
            }
        });
        return taskDefinitionKeys;
    }

    /**
     * 将指定任务委派给其他人处理，等接收人处理后再回到原审批人手中审批
     *
     * @param userId 用户编号
     * @param reqVO  被委派人和被委派的任务编号理由参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delegateTask(Long userId, BpmTaskDelegateDTO reqVO) {
        String taskId = reqVO.getId();
        // 1.1 校验任务
        Task task = validateTask(userId, reqVO.getId());
        if (task.getAssignee().equals(reqVO.getDelegateUserId().toString())) { // 校验当前审批人和被委派人不是同一人
            throw exception(TASK_DELEGATE_FAIL_USER_REPEAT);
        }
        // 1.2 校验目标用户存在
        SysUserDTO delegateUser = sysUserService.getById(reqVO.getDelegateUserId());
        if (delegateUser == null) {
            throw exception(TASK_DELEGATE_FAIL_USER_NOT_EXISTS);
        }

        // 2. 添加委托意见
        SysUserDTO currentUser = sysUserService.getById(userId);
        taskService.addComment(taskId, task.getProcessInstanceId(), BpmCommentTypeEnum.DELEGATE_START.getType(),
                BpmCommentTypeEnum.DELEGATE_START.formatComment(currentUser.getUserName(), delegateUser.getUserName(), reqVO.getReason()));

        // 3.1 设置任务所有人 (owner) 为原任务的处理人 (assignee)
        // 特殊：如果已经被委派（owner 非空），则不需要更新 owner：https://gitee.com/zhijiantianya/yudao-cloud/issues/ICJ153
        if (StrUtil.isEmpty(task.getOwner())) {
            taskService.setOwner(taskId, task.getAssignee());
        }
        // 省略调默认消息推送
        taskService.setVariableLocal(taskId, "SKIP_DEFAULT_NOTIFY", true);
        // 3.2 执行委派，将任务委派给 delegateUser
        taskService.delegateTask(taskId, reqVO.getDelegateUserId().toString());
        // 补充说明：委托不单独设置状态。如果需要，可通过 Task 的 DelegationState 字段，判断是否为 DelegationState.PENDING 委托中
        // 3.3 发送消息提醒
        SysUserDTO delegateUserDto = sysUserService.getById(reqVO.getDelegateUserId());
        if (delegateUserDto != null && StrUtil.isNotBlank(delegateUserDto.getUserAccount())) {
            WebSocketUtils.sendTemplateNotification(
                    delegateUserDto.getUserAccount(),
                    BpmMessageConstants.TASK_DELEGATE,
                    currentUser.getUserName(),
                    task.getName()
            );
        }
    }

    /**
     * 将流程任务分配给指定用户
     *
     * @param userId 用户编号
     * @param reqVO  分配请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transferTask(Long userId, BpmTaskTransferDTO reqVO) {
        String taskId = reqVO.getId();
        // 1.1 校验任务
        Task task = validateTask(userId, reqVO.getId());
        if (task.getAssignee().equals(reqVO.getAssigneeUserId().toString())) { // 校验当前审批人和被转派人不是同一人
            throw exception(TASK_TRANSFER_FAIL_USER_REPEAT);
        }
        // 1.2 校验目标用户存在
        SysUserDTO assigneeUser = sysUserService.getById(reqVO.getAssigneeUserId());
        if (assigneeUser == null) {
            throw exception(TASK_TRANSFER_FAIL_USER_NOT_EXISTS);
        }

        // 2. 添加委托意见
        SysUserDTO currentUser = sysUserService.getById(userId);
        taskService.addComment(taskId, task.getProcessInstanceId(), BpmCommentTypeEnum.TRANSFER.getType(),
                BpmCommentTypeEnum.TRANSFER.formatComment(currentUser.getUserName(), assigneeUser.getUserName(), reqVO.getReason()));

        // 3.1 设置任务所有人 (owner) 为原任务的处理人 (assignee)
        // 特殊：如果已经被转派（owner 非空），则不需要更新 owner：https://gitee.com/zhijiantianya/yudao-cloud/issues/ICJ153
        if (StrUtil.isEmpty(task.getOwner())) {
            taskService.setOwner(taskId, task.getAssignee());
        }
        // 省略调默认消息推送
        taskService.setVariableLocal(taskId, "SKIP_DEFAULT_NOTIFY", true);
        // 3.2 执行转派（审批人），将任务转派给 assigneeUser
        // 委托（ delegate）和转派（transfer）的差别，就在这块的调用！！！！
        taskService.setAssignee(taskId, reqVO.getAssigneeUserId().toString());
        // 3.3 发送消息提醒
        if (StrUtil.isNotBlank(assigneeUser.getUserAccount())) {
            WebSocketUtils.sendTemplateNotification(
                    assigneeUser.getUserAccount(),
                    BpmMessageConstants.TASK_TRANSFER,
                    currentUser.getUserName(),
                    task.getName()
            );
        }
    }

    /**
     * 将指定流程实例的、进行中的流程任务，移动到结束节点
     *
     * @param processInstanceId 流程编号
     * @param reason 原因
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moveTaskToEnd(String processInstanceId, String reason) {
        List<Task> taskList = getRunningTaskListByProcessInstanceId(processInstanceId, null, null);
        if (CollUtil.isEmpty(taskList)) {
            return;
        }

        // 1. 其它未结束的任务，直接取消
        // 疑问：为什么不通过 updateTaskStatusWhenCanceled 监听取消，而是直接提前调用呢？
        // 回答：详细见 updateTaskStatusWhenCanceled 的方法，加签的场景
        taskList.forEach(task -> {
            Integer otherTaskStatus = (Integer) task.getTaskLocalVariables().get(BpmnVariableConstants.TASK_VARIABLE_STATUS);
            if (BpmTaskStatusEnum.isEndStatus(otherTaskStatus)) {
                return;
            }
            processTaskCanceled(task.getId());
        });

        // 2. 终止流程
        BpmnModel bpmnModel = modelService.getBpmnModelByDefinitionId(taskList.get(0).getProcessDefinitionId());
        List<String> activityIds = CollUtil.newArrayList(convertSet(taskList, Task::getTaskDefinitionKey));
        EndEvent endEvent = BpmnModelUtils.getEndEvent(bpmnModel);
        Assert.notNull(endEvent, "结束节点不能为空");
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(processInstanceId)
                .moveActivityIdsToSingleActivityId(activityIds, endEvent.getId())
                .changeState();

        // 3. 特殊：如果跳转到 EndEvent 流程还未结束， 执行 deleteProcessInstance 方法
        // TODO 芋艿：目前发现并行分支情况下，会存在这个情况，后续看看有没更好的方案；
        List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).list();
        if (CollUtil.isNotEmpty(executions)) {
            log.warn("[moveTaskToEnd][执行跳转到 EndEvent 后, 流程实例未结束，强制执行 deleteProcessInstance 方法]");
            runtimeService.deleteProcessInstance(processInstanceId, reason);
        }
    }

    /**
     * 任务加签
     *
     * @param userId 被加签的用户和任务 ID，加签类型
     * @param reqVO  当前用户 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createSignTask(Long userId, BpmTaskSignCreateDTO reqVO) {
        // 1. 获取和校验任务
        TaskEntityImpl taskEntity = validateTaskCanCreateSign(userId, reqVO);
        List<SysUserDTO> userList = sysUserService.getUserList(reqVO.getUserIds());
        if (CollUtil.isEmpty(userList)) {
            throw exception(TASK_SIGN_CREATE_USER_NOT_EXIST);
        }

        // 2. 处理当前任务
        // 2.1 开启计数功能，主要用于为了让表 ACT_RU_TASK 中的 SUB_TASK_COUNT_ 字段记录下总共有多少子任务，后续可能有用
        taskEntity.setCountEnabled(true);
        // 2.2 向前加签，设置 owner，置空 assign。等子任务都完成后，再调用 resolveTask 重新将 owner 设置为 assign
        // 原因是：不能和向前加签的子任务一起审批，需要等前面的子任务都完成才能审批
        if (reqVO.getType().equals(BpmTaskSignTypeEnum.BEFORE.getType())) {
            taskEntity.setOwner(taskEntity.getAssignee());
            taskEntity.setAssignee(null);
        }
        // 2.4 记录加签方式，完成任务时需要用到判断
        taskEntity.setScopeType(reqVO.getType());
        // 2.5 保存当前任务修改后的值
        taskService.saveTask(taskEntity);
        // 2.6 更新 task 状态为 WAIT，只有在向前加签的时候
        if (reqVO.getType().equals(BpmTaskSignTypeEnum.BEFORE.getType())) {
            updateTaskStatus(taskEntity.getId(), BpmTaskStatusEnum.WAIT.getStatus());
        }

        // 3. 创建加签任务
        createSignTaskList(convertList(reqVO.getUserIds(), String::valueOf), taskEntity);

        // 4. 记录加签的评论到 task 任务
        SysUserDTO currentUser = sysUserService.getById(userId);
        String comment = StrUtil.format(BpmCommentTypeEnum.ADD_SIGN.getComment(),
                currentUser.getUserName(), BpmTaskSignTypeEnum.nameOfType(reqVO.getType()),
                String.join(",", convertList(userList, SysUserDTO::getUserName)), reqVO.getReason());
        taskService.addComment(reqVO.getId(), taskEntity.getProcessInstanceId(), BpmCommentTypeEnum.ADD_SIGN.getType(), comment);
    }

    /**
     * 校验任务是否可以加签，主要校验加签类型是否一致：
     * <p>
     * 1. 如果存在“向前加签”的任务，则不能“向后加签”
     * 2. 如果存在“向后加签”的任务，则不能“向前加签”
     *
     * @param userId 当前用户 ID
     * @param reqVO  请求参数，包含任务 ID 和加签类型
     * @return 当前任务
     */
    private TaskEntityImpl validateTaskCanCreateSign(Long userId, BpmTaskSignCreateDTO reqVO) {
        TaskEntityImpl taskEntity = (TaskEntityImpl) validateTask(userId, reqVO.getId());
        // 向前加签和向后加签不能同时存在
        if (taskEntity.getScopeType() != null
                && ObjectUtil.notEqual(taskEntity.getScopeType(), reqVO.getType())) {
            throw exception(TASK_SIGN_CREATE_TYPE_ERROR,
                    BpmTaskSignTypeEnum.nameOfType(taskEntity.getScopeType()), BpmTaskSignTypeEnum.nameOfType(reqVO.getType()));
        }

        // 同一个 key 的任务，审批人不重复
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(taskEntity.getProcessInstanceId())
                .taskDefinitionKey(taskEntity.getTaskDefinitionKey()).list();
        List<Long> currentAssigneeList = convertListByFlatMap(taskList, task -> // 需要考虑 owner 的情况，因为向后加签时，它暂时没 assignee 而是 owner
                Stream.of(NumberUtils.parseLong(task.getAssignee()), NumberUtils.parseLong(task.getOwner())));
        if (CollUtil.containsAny(currentAssigneeList, reqVO.getUserIds())) {
            List<SysUserDTO> userList = sysUserService.getUserList(CollUtil.intersection(currentAssigneeList, reqVO.getUserIds()));
            throw exception(TASK_SIGN_CREATE_USER_REPEAT, String.join(",", convertList(userList, SysUserDTO::getUserName)));
        }
        return taskEntity;
    }

    /**
     * 创建加签子任务
     *
     * @param userIds    被加签的用户 ID
     * @param taskEntity 被加签的任务
     */
    private void createSignTaskList(List<String> userIds, TaskEntityImpl taskEntity) {
        if (CollUtil.isEmpty(userIds)) {
            return;
        }
        // 创建加签人的新任务，全部基于 taskEntity 为父任务来创建
        for (String addSignId : userIds) {
            if (StrUtil.isBlank(addSignId)) {
                continue;
            }
            createSignTask(taskEntity, addSignId);
        }
    }

    /**
     * 创建加签子任务
     *
     * @param parentTask 父任务
     * @param assignee   子任务的执行人
     */
    private void createSignTask(TaskEntityImpl parentTask, String assignee) {
        // 1. 生成子任务
        TaskEntityImpl task = (TaskEntityImpl) taskService.newTask(IdUtil.fastSimpleUUID());
        BpmTaskConvert.INSTANCE.copyTo(parentTask, task);

        // 2.1 向前加签，设置审批人
        if (BpmTaskSignTypeEnum.BEFORE.getType().equals(parentTask.getScopeType())) {
            task.setAssignee(assignee);
            // 2.2 向后加签，设置 owner 不设置 assignee 是因为不能同时审批，需要等父任务完成
        } else {
            task.setOwner(assignee);
        }
        // 2.3 保存子任务
        taskService.saveTask(task);

        // 3. 向后前签，设置子任务的状态为 WAIT，因为需要等父任务审批完
        if (BpmTaskSignTypeEnum.AFTER.getType().equals(parentTask.getScopeType())) {
            updateTaskStatus(task.getId(), BpmTaskStatusEnum.WAIT.getStatus());
        }
    }

    /**
     * 任务减签
     *
     * @param userId 当前用户ID
     * @param reqVO  被减签的任务 ID，理由
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSignTask(Long userId, BpmTaskSignDeleteDTO reqVO) {
        // 1.1 校验 task 可以被减签
        Task task = validateTaskCanSignDelete(reqVO.getId());
        // 1.2 校验取消人存在
        SysUserDTO cancelUser = null;
        if (StrUtil.isNotBlank(task.getAssignee())) {
            cancelUser = sysUserService.getById(NumberUtils.parseLong(task.getAssignee()));
        }
        if (cancelUser == null && StrUtil.isNotBlank(task.getOwner())) {
            cancelUser = sysUserService.getById(NumberUtils.parseLong(task.getOwner()));
        }
        Assert.notNull(cancelUser, "任务中没有所有者和审批人，数据错误");

        // 2.1 获得子任务列表，包括子任务的子任务
        List<Task> childTaskList = getAllChildTaskList(task);
        childTaskList.add(task);
        // 2.2 更新子任务为已取消
        String cancelReason = StrUtil.format("任务被取消，原因：由于[{}]操作[减签]，", cancelUser.getUserName());
        childTaskList.forEach(childTask -> updateTaskStatusAndReason(childTask.getId(), BpmTaskStatusEnum.CANCEL.getStatus(), cancelReason));
        // 2.3 删除任务和所有子任务
        taskService.deleteTasks(convertList(childTaskList, Task::getId));

        // 3. 记录日志到父任务中。先记录日志是因为，通过 handleParentTask 方法之后，任务可能被完成了，并且不存在了，会报异常，所以先记录
        SysUserDTO user = sysUserService.getById(userId);
        taskService.addComment(task.getParentTaskId(), task.getProcessInstanceId(), BpmCommentTypeEnum.SUB_SIGN.getType(),
                StrUtil.format(BpmCommentTypeEnum.SUB_SIGN.getComment(), user.getUserName(), cancelUser.getUserName()));

        // 4. 处理当前任务的父任务
        handleParentTaskIfSign(task.getParentTaskId());
    }

    /**
     * 抄送任务
     *
     * @param userId 用户编号
     * @param reqVO  通过请求
     */
    @Override
    public void copyTask(Long userId, BpmTaskCopyDTO reqVO) {
        processInstanceCopyService.createProcessInstanceCopy(reqVO.getCopyUserIds(), reqVO.getReason(), reqVO.getId());
    }

    /**
     * 撤回任务
     *
     * @param userId 用户编号
     * @param taskId 任务编号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void withdrawTask(Long userId, String taskId) {
        // 1.1 查询本人已办任务
        HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery()
                .taskId(taskId).taskAssignee(userId.toString()).finished().singleResult();
        if (ObjUtil.isNull(taskInstance)) {
            throw exception(TASK_WITHDRAW_FAIL_TASK_NOT_EXISTS);
        }
        // 1.2 校验流程是否结束
        ProcessInstance processInstance = processInstanceService.getProcessInstance(taskInstance.getProcessInstanceId());
        if (ObjUtil.isNull(processInstance)) {
            throw exception(TASK_WITHDRAW_FAIL_PROCESS_NOT_RUNNING);
        }
        // 1.3 判断此流程是否允许撤回
        BpmProcessDefinitionInfoPO processDefinitionInfo = bpmProcessDefinitionService.getProcessDefinitionInfo(
                processInstance.getProcessDefinitionId());
        if (ObjUtil.isNull(processDefinitionInfo) || !Boolean.TRUE.equals(processDefinitionInfo.getAllowWithdrawTask())) {
            throw exception(TASK_WITHDRAW_FAIL_NOT_ALLOW);
        }
        // 1.4 判断下一个节点是否被审批过，如果是则无法撤回
        BpmnModel bpmnModel = modelService.getBpmnModelByDefinitionId(taskInstance.getProcessDefinitionId());
        UserTask userTask = (UserTask) BpmnModelUtils.getFlowElementById(bpmnModel, taskInstance.getTaskDefinitionKey());
        List<String> nextUserTaskKeys = convertList(BpmnModelUtils.getNextUserTasks(userTask), UserTask::getId);
        if (CollUtil.isEmpty(nextUserTaskKeys)) {
            throw exception(TASK_WITHDRAW_FAIL_NEXT_TASK_NOT_ALLOW);
        }
        // TODO @芋艿：是否选择升级flowable版本解决taskCreatedAfter、taskCreatedBefore问题，升级7.1.0可以；包括 todo 和 done 那边的查询哇？？？ 是的！
        long nextUserTaskFinishedCount = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstance.getProcessInstanceId()).taskDefinitionKeys(nextUserTaskKeys)
                .taskCreatedAfter(taskInstance.getEndTime()).finished().count();
        if (nextUserTaskFinishedCount > 0) {
            throw exception(TASK_WITHDRAW_FAIL_NEXT_TASK_NOT_ALLOW);
        }
        // 1.5 获取需要撤回的运行任务
        List<Task> runningTasks = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId())
                .taskDefinitionKeys(nextUserTaskKeys).active().list();
        if (CollUtil.isEmpty(runningTasks)) {
            throw exception(TASK_WITHDRAW_FAIL_NEXT_TASK_NOT_ALLOW);
        }

        // 2.1 取消当前任务
        List<String> withdrawExecutionIds = new ArrayList<>();
        for (Task task : runningTasks) {
            // 标记撤回任务为取消
            taskService.addComment(task.getId(), taskInstance.getProcessInstanceId(), BpmCommentTypeEnum.CANCEL.getType(),
                    BpmCommentTypeEnum.CANCEL.formatComment("前一节点撤回"));
            updateTaskStatusAndReason(task.getId(), BpmTaskStatusEnum.CANCEL.getStatus(), BpmReasonEnum.CANCEL_BY_WITHDRAW.getReason());
            withdrawExecutionIds.add(task.getExecutionId());
        }
        // 2.2 执行撤回操作
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(processInstance.getProcessInstanceId())
                .moveExecutionsToSingleActivityId(withdrawExecutionIds, taskInstance.getTaskDefinitionKey())
                .changeState();
    }

    /**
     * 校验任务是否能被减签
     *
     * @param id 任务编号
     * @return 任务信息
     */
    private Task validateTaskCanSignDelete(String id) {
        Task task = validateTaskExist(id);
        if (task.getParentTaskId() == null) {
            throw exception(TASK_SIGN_DELETE_NO_PARENT);
        }
        Task parentTask = getTask(task.getParentTaskId());
        if (parentTask == null) {
            throw exception(TASK_SIGN_DELETE_NO_PARENT);
        }
        if (BpmTaskSignTypeEnum.of(parentTask.getScopeType()) == null) {
            throw exception(TASK_SIGN_DELETE_NO_PARENT);
        }
        return task;
    }

    // ========== Event 事件相关方法 ==========

    /**
     * 处理 Task 创建事件，目前是
     * <p>
     * 1. 更新它的状态为审批中
     * 2. 处理自动通过的情况，例如说：1）无审批人时，是否自动通过、不通过；2）非【人工审核】时，是否自动通过、不通过
     * <p>
     * 注意：它的触发时机，晚于 {@link #processTaskAssigned(Task)} 之后
     *
     * @param task 任务实体
     */
    @Override
    public void processTaskCreated(Task task) {
        // 1. 设置为待办中
        Integer status = (Integer) task.getTaskLocalVariables().get(BpmnVariableConstants.TASK_VARIABLE_STATUS);
        if (status != null) {
            log.error("[updateTaskStatusWhenCreated][taskId({}) 已经有状态({})]", task.getId(), status);
            return;
        }
        updateTaskStatus(task.getId(), BpmTaskStatusEnum.RUNNING.getStatus());

        ProcessInstance processInstance = processInstanceService.getProcessInstance(task.getProcessInstanceId());
        if (processInstance == null) {
            log.error("[processTaskCreated][taskId({}) 没有找到流程实例]", task.getId());
            return;
        }
        BpmProcessDefinitionInfoPO processDefinitionInfo = bpmProcessDefinitionService.
                getProcessDefinitionInfo(processInstance.getProcessDefinitionId());
        if (processDefinitionInfo == null) {
            log.error("[processTaskCreated][processDefinitionId({}) 没有找到流程定义]", processInstance.getProcessDefinitionId());
            return;
        }

        // 2. 任务前置通知
//        if (ObjUtil.isNotNull(processDefinitionInfo.getTaskBeforeTriggerSetting())) {
//            BpmModelMetaInfoDTO.HttpRequestSetting setting = processDefinitionInfo.getTaskBeforeTriggerSetting();
//            BpmHttpRequestUtils.executeBpmHttpRequest(processInstance,
//                    setting.getUrl(), setting.getHeader(), setting.getBody(), true, setting.getResponse());
//        }

        // 3. 处理自动通过的情况，例如说：1）无审批人时，是否自动通过、不通过；2）非【人工审核】时，是否自动通过、不通过
        BpmnModel bpmnModel = modelService.getBpmnModelByDefinitionId(processInstance.getProcessDefinitionId());
        FlowElement userTaskElement = BpmnModelUtils.getFlowElementById(bpmnModel, task.getTaskDefinitionKey());
        Integer approveType = BpmnModelUtils.parseApproveType(userTaskElement);
        Integer assignEmptyHandlerType = BpmnModelUtils.parseAssignEmptyHandlerType(userTaskElement);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

            /**
             * 特殊情况：部分情况下，TransactionSynchronizationManager 注册 afterCommit 监听时，不会被调用，但是 afterCompletion 可以
             * 例如说：第一个 task 就是配置【自动通过】或者【自动拒绝】时
             * 参见 <a href="https://gitee.com/zhijiantianya/yudao-cloud/issues/IB7V7Q">issue</a> 反馈
             */
            @Override
            public void afterCompletion(int transactionStatus) {
                // 回滚情况，直接返回
                if (ObjectUtil.equal(transactionStatus, TransactionSynchronization.STATUS_ROLLED_BACK)) {
                    return;
                }
                // 特殊情况：第一个 task 【自动通过】时，第二个任务设置审批人时 transactionStatus 会为 STATUS_UNKNOWN，不知道啥原因
                if (ObjectUtil.equal(transactionStatus, TransactionSynchronization.STATUS_UNKNOWN)
                        && getTask(task.getId()) == null) {
                    return;
                }
                // 特殊情况一：【人工审核】审批人为空，根据配置是否要自动通过、自动拒绝
                if (ObjectUtil.equal(approveType, BpmUserTaskApproveTypeEnum.USER.getType())) {
                    // 如果有审批人、或者拥有人，则说明不满足情况一，不自动通过、不自动拒绝
                    if (!ObjectUtil.isAllEmpty(task.getAssignee(), task.getOwner())) {
                        return;
                    }
                    if (ObjectUtil.equal(assignEmptyHandlerType, BpmUserTaskAssignEmptyHandlerTypeEnum.APPROVE.getType())) {
                        getSelf().approveTask(null, new BpmTaskApproveDTO()
                                .setId(task.getId()).setReason(BpmReasonEnum.ASSIGN_EMPTY_APPROVE.getReason()));
                    } else if (ObjectUtil.equal(assignEmptyHandlerType, BpmUserTaskAssignEmptyHandlerTypeEnum.REJECT.getType())) {
                        getSelf().rejectTask(null, new BpmTaskRejectDTO()
                                .setId(task.getId()).setReason(BpmReasonEnum.ASSIGN_EMPTY_REJECT.getReason()));
                    }
                    // 特殊情况二：【自动审核】审批类型为自动通过、不通过
                } else {
                    if (ObjectUtil.equal(approveType, BpmUserTaskApproveTypeEnum.AUTO_APPROVE.getType())) {
                        getSelf().approveTask(null, new BpmTaskApproveDTO()
                                .setId(task.getId()).setReason(BpmReasonEnum.APPROVE_TYPE_AUTO_APPROVE.getReason()));
                    } else if (ObjectUtil.equal(approveType, BpmUserTaskApproveTypeEnum.AUTO_REJECT.getType())) {
                        getSelf().rejectTask(null, new BpmTaskRejectDTO()
                                .setId(task.getId()).setReason(BpmReasonEnum.APPROVE_TYPE_AUTO_REJECT.getReason()));
                    }
                }
            }

        });

        // 更新业务中间表（事务提交后执行）（事务同步器TransactionSynchronization）
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    updateBusinessInstanceCurrentInfo(task.getProcessInstanceId());

                    // 推送 WebSocket 消息
                    if (StrUtil.isBlank(task.getAssignee())) {
                        // 获取本地变量，看是否被打上了标记
                        Boolean skipNotify = (Boolean) taskService.getVariableLocal(task.getId(), "SKIP_DEFAULT_NOTIFY");
                        // 如果没有标记，说明是正常的流程流转，正常发送“待办”消息
                        if (skipNotify == null || !skipNotify) {
                            ProcessInstance pi = processInstanceService.getProcessInstance(task.getProcessInstanceId());
                            if (pi != null) {
                                pushWebSocketNotification(task, pi, true);
                            }
                        } else {
                            log.info("[WebSocket推送] 检测到转办/委派消息，跳过发送默认待办通知, taskId: {}", task.getId());
                        }
                    }
                }
            });
        } else {
            // 如果没有事务（理论上不可能），则直接执行
            updateBusinessInstanceCurrentInfo(task.getProcessInstanceId());
        }
    }

    /**
     * 计算并更新关联表的当前节点信息
     *
     */
    private void updateBusinessInstanceCurrentInfo(String processInstanceId) {
        // 1. 查询该流程实例下所有“活跃”的任务 (包括当前刚刚创建的这个)
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .active()
                .list();

        // 2.. 如果没有活跃任务，说明流程可能结束了（会由 ProcessInstanceListener 处理），直接返回
        if (CollUtil.isEmpty(tasks)) {
            return;
        }

        // 3. 拼接节点名称 (去重)
        String currentNodeNames = tasks.stream()
                .map(Task::getName)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .collect(Collectors.joining(","));

        // 4.解析处理人
        Set<String> approverNameSet = new LinkedHashSet<>();

        for (Task task : tasks) {
            // 情况 A：有受理人 (Assignee)
            if (StrUtil.isNotBlank(task.getAssignee())) {
                SysUserDTO user = sysUserService.getById(NumberUtils.parseLong(task.getAssignee()));
                if (user != null) {
                    approverNameSet.add(user.getUserName());
                }
            }
            // 情况 B：无受理人，查找候选关系 (IdentityLinks)
            else {
                List<IdentityLink> links = taskService.getIdentityLinksForTask(task.getId());
                if (CollUtil.isNotEmpty(links)) {
                    for (IdentityLink link : links) {
                        // todo 这里可能是岗位也可能是角色，目前只处理了角色
                        if (StrUtil.isNotBlank(link.getGroupId())) {
                            SysRoleDTO role = sysRoleService.getById(NumberUtils.parseLong(link.getGroupId()));
                            if (role != null) {
                                approverNameSet.add("角色:" + role.getRoleName());
                            }
                        }
                    }
                }
            }
        }

        // 拼接结果
        String approverNames = CollUtil.join(approverNameSet, ",");

        // 兜底显示
        if (StrUtil.isBlank(approverNames)) {
            approverNames = "待定";
        }

        // 5. 执行更新 SQL
        BpmBusinessInstanceDTO bpmBusinessInstanceDTO = new BpmBusinessInstanceDTO();
        bpmBusinessInstanceDTO.setProcInstId(processInstanceId);
        bpmBusinessInstanceDTO.setCurrentNodeName(currentNodeNames);
        bpmBusinessInstanceDTO.setApproverNames(approverNames);
        bpmBusinessInstanceMapper.updateByProcInstId(bpmBusinessInstanceDTO);
    }

    /**
     * 重要补充说明：该方法目前主要有两个情况会调用到：
     * <p>
     * 1. 或签场景 + 审批通过：一个或签有多个审批时，如果 A 审批通过，其它或签 B、C 等任务会被 Flowable 自动删除，此时需要通过该方法更新状态为已取消
     * 2. 审批不通过：在 {@link #rejectTask(Long, BpmTaskRejectDTO)} 不通过时，对于加签的任务，不会被 Flowable 删除，此时需要通过该方法更新状态为已取消
     */
    @Override
    public void processTaskCanceled(String taskId) {
        Task task = getTask(taskId);
        // 1. 可能只是活动，不是任务，所以查询不到
        if (task == null) {
            log.error("[updateTaskStatusWhenCanceled][taskId({}) 任务不存在]", taskId);
            return;
        }

        // 2. 更新 task 状态 + 原因
        Integer status = (Integer) task.getTaskLocalVariables().get(BpmnVariableConstants.TASK_VARIABLE_STATUS);
        if (BpmTaskStatusEnum.isEndStatus(status)) {
            log.error("[updateTaskStatusWhenCanceled][taskId({}) 处于结果({})，无需进行更新]", taskId, status);
            return;
        }
        updateTaskStatusAndReason(taskId, BpmTaskStatusEnum.CANCEL.getStatus(), BpmReasonEnum.CANCEL_BY_SYSTEM.getReason());
        // 补充说明：由于 Task 被删除成 HistoricTask 后，无法通过 taskService.addComment 添加理由，所以无法存储具体的取消理由
    }

    /**
     * 处理 Task 设置审批人事件，目前是发送审批消息
     *
     * @param task 任务实体
     */
    @Override
    public void processTaskAssigned(Task task) {
        // 发送通知。在事务提交时，批量执行操作，所以直接查询会无法查询到 ProcessInstance，所以这里是通过监听事务的提交来实现。
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

            /**
             * 特殊情况：部分情况下，TransactionSynchronizationManager 注册 afterCommit 监听时，不会被调用，但是 afterCompletion 可以
             * 例如说：第一个 task 就是配置【自动通过】或者【自动拒绝】时
             * 参见 <a href="https://gitee.com/zhijiantianya/yudao-cloud/issues/IB7V7Q">issue</a> 反馈
             */
            @Override
            public void afterCompletion(int transactionStatus) {
                // 回滚情况，直接返回
                if (ObjectUtil.equal(transactionStatus, TransactionSynchronization.STATUS_ROLLED_BACK)) {
                    return;
                }
                // 特殊情况：第一个 task 【自动通过】时，第二个任务设置审批人时 transactionStatus 会为 STATUS_UNKNOWN，不知道啥原因
                if (ObjectUtil.equal(transactionStatus, TransactionSynchronization.STATUS_UNKNOWN)
                        && getTask(task.getId()) == null) {
                    return;
                }
                // 更新业务关联表
                try {
                    updateBusinessInstanceCurrentInfo(task.getProcessInstanceId());
                } catch (Exception e) {
                    log.error("[processTaskAssigned][更新业务中间表失败，processInstanceId({})]", task.getProcessInstanceId(), e);
                }
                // 只要分配了具体的办理人，就触发 WebSocket 发送
                if (StrUtil.isNotBlank(task.getAssignee())) {
                    // 获取本地变量，看是否被打上了标记
                    Boolean skipNotify = (Boolean) taskService.getVariableLocal(task.getId(), "SKIP_DEFAULT_NOTIFY");
                    // 如果没有标记，说明是正常的流程流转，正常发送“待办”消息
                    if (skipNotify == null || !skipNotify) {
                        ProcessInstance pi = processInstanceService.getProcessInstance(task.getProcessInstanceId());
                        if (pi != null) {
                            pushWebSocketNotification(task, pi, false);
                        }
                    } else {
                        log.info("[WebSocket推送] 检测到转办/委派消息，跳过发送默认待办通知, taskId: {}", task.getId());
                    }
                }
                if (StrUtil.isEmpty(task.getAssignee())) {
                    log.error("[processTaskAssigned][taskId({}) 没有分配到负责人]", task.getId());
                    return;
                }
                ProcessInstance processInstance = processInstanceService.getProcessInstance(task.getProcessInstanceId());
                if (processInstance == null) {
                    log.error("[processTaskAssigned][taskId({}) 没有找到流程实例]", task.getId());
                    return;
                }

                // 自动去重，通过自动审批的方式
                BpmProcessDefinitionInfoPO processDefinitionInfo = bpmProcessDefinitionService.getProcessDefinitionInfo(task.getProcessDefinitionId());
                if (processDefinitionInfo == null) {
                    log.error("[processTaskAssigned][taskId({}) 没有找到流程定义({})]", task.getId(), task.getProcessDefinitionId());
                    return;
                }
                if (processDefinitionInfo.getAutoApprovalType() != null) {
                    HistoricTaskInstanceQuery sameAssigneeQuery = historyService.createHistoricTaskInstanceQuery()
                            .processInstanceId(task.getProcessInstanceId())
                            .taskAssignee(task.getAssignee()) // 相同审批人
                            .taskVariableValueEquals(BpmnVariableConstants.TASK_VARIABLE_STATUS, BpmTaskStatusEnum.APPROVE.getStatus())
                            .finished();
                    if (BpmAutoApproveTypeEnum.APPROVE_ALL.getType().equals(processDefinitionInfo.getAutoApprovalType())
                            && sameAssigneeQuery.count() > 0) {
                        getSelf().approveTask(Long.valueOf(task.getAssignee()), new BpmTaskApproveDTO().setId(task.getId())
                                .setReason(BpmAutoApproveTypeEnum.APPROVE_ALL.getName()));
                        return;
                    }
                    if (BpmAutoApproveTypeEnum.APPROVE_SEQUENT.getType().equals(processDefinitionInfo.getAutoApprovalType())) {
                        BpmnModel bpmnModel = modelService.getBpmnModelByDefinitionId(processInstance.getProcessDefinitionId());
                        if (bpmnModel == null) {
                            log.error("[processTaskAssigned][taskId({}) 没有找到流程模型({})]", task.getId(), task.getProcessDefinitionId());
                            return;
                        }
                        List<String> sourceTaskIds = convertList(BpmnModelUtils.getElementIncomingFlows( // 获取所有上一个节点
                                        BpmnModelUtils.getFlowElementById(bpmnModel, task.getTaskDefinitionKey())),
                                SequenceFlow::getSourceRef);
                        if (sameAssigneeQuery.taskDefinitionKeys(sourceTaskIds).count() > 0) {
                            getSelf().approveTask(Long.valueOf(task.getAssignee()), new BpmTaskApproveDTO().setId(task.getId())
                                    .setReason(BpmAutoApproveTypeEnum.APPROVE_SEQUENT.getName()));
                            return;
                        }
                    }
                }

                // 获取发起人节点
                BpmnModel bpmnModel = modelService.getBpmnModelByDefinitionId(processInstance.getProcessDefinitionId());
                if (bpmnModel == null) {
                    log.error("[processTaskAssigned][taskId({}) 没有找到流程模型]", task.getId());
                    return;
                }
                FlowElement userTaskElement = BpmnModelUtils.getFlowElementById(bpmnModel, task.getTaskDefinitionKey());
                // 判断是否为退回或者驳回：如果是退回或者驳回不走这个策略（使用 local variable）
                Boolean returnTaskFlag = runtimeService.getVariableLocal(task.getExecutionId(),
                        String.format(BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_RETURN_FLAG, task.getTaskDefinitionKey()), Boolean.class);
                Boolean skipStartUserNodeFlag = Convert.toBool(runtimeService.getVariable(processInstance.getProcessInstanceId(),
                        BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_SKIP_START_USER_NODE, String.class));
                if (userTaskElement.getId().equals(START_USER_NODE_ID)
                        && (skipStartUserNodeFlag == null // 目的：一般是“主流程”，发起人节点，自动通过审核
                        || BooleanUtil.isTrue(skipStartUserNodeFlag)) // 目的：一般是“子流程”，发起人节点，按配置自动通过审核
                        && ObjUtil.notEqual(returnTaskFlag, Boolean.TRUE)) {
                    getSelf().approveTask(Long.valueOf(task.getAssignee()), new BpmTaskApproveDTO().setId(task.getId())
                            .setReason(BpmReasonEnum.ASSIGN_START_USER_APPROVE_WHEN_SKIP_START_USER_NODE.getReason()));
                    return;
                }
                // 当不为发起人节点时，审批人与提交人为同一人时，根据 BpmUserTaskAssignStartUserHandlerTypeEnum 策略进行处理
                if (ObjectUtil.notEqual(userTaskElement.getId(), START_USER_NODE_ID)
                        && StrUtil.equals(task.getAssignee(), processInstance.getStartUserId())) {
                    if (ObjUtil.notEqual(returnTaskFlag, Boolean.TRUE)) {
                        Integer assignStartUserHandlerType = BpmnModelUtils.parseAssignStartUserHandlerType(userTaskElement);

                        // 情况一：自动跳过
                        if (ObjectUtils.equalsAny(assignStartUserHandlerType,
                                BpmUserTaskAssignStartUserHandlerTypeEnum.SKIP.getType())) {
                            getSelf().approveTask(Long.valueOf(task.getAssignee()), new BpmTaskApproveDTO().setId(task.getId())
                                    .setReason(BpmReasonEnum.ASSIGN_START_USER_APPROVE_WHEN_SKIP.getReason()));
                            return;
                        }
                        // 情况二：转交给部门负责人审批
                        if (ObjectUtils.equalsAny(assignStartUserHandlerType,
                                BpmUserTaskAssignStartUserHandlerTypeEnum.TRANSFER_DEPT_LEADER.getType())) {
                            SysUserDTO startUser = sysUserService.getById(Long.valueOf(processInstance.getStartUserId()));
                            Assert.notNull(startUser, "提交人({})信息为空", processInstance.getStartUserId());
                            SysDeptDTO dept = startUser.getDeptId() != null ? sysDeptService.getById(startUser.getDeptId()) : null;
                            Assert.notNull(dept, "提交人({})部门({})信息为空", processInstance.getStartUserId(), startUser.getDeptId());
                            // 找不到部门负责人的情况下，自动审批通过
                            // noinspection DataFlowIssue
                            // todo 目前没有部门负责人，所以这里自动跳过
                            if (true) {
                                getSelf().approveTask(Long.valueOf(task.getAssignee()), new BpmTaskApproveDTO().setId(task.getId())
                                        .setReason(BpmReasonEnum.ASSIGN_START_USER_APPROVE_WHEN_DEPT_LEADER_NOT_FOUND.getReason()));
                                return;
                            }
                            // 找得到部门负责人的情况下，修改负责人
//                            if (ObjectUtil.notEqual(dept.getLeaderUserId(), startUser.getId())) {
//                                getSelf().transferTask(Long.valueOf(task.getAssignee()), new BpmTaskTransferDTO()
//                                        .setId(task.getId()).setAssigneeUserId(dept.getLeaderUserId())
//                                        .setReason(BpmReasonEnum.ASSIGN_START_USER_TRANSFER_DEPT_LEADER.getReason()));
//                                return;
//                            }
                            // 如果部门负责人是自己，还是自己审批吧~
                        }
                    }
                }
                // 注意：需要基于 instance 设置租户编号，避免 Flowable 内部异步时，丢失租户编号
//                FlowableUtils.execute(processInstance.getTenantId(), () -> {
//                    SysUserDTO startUser = adminUserApi.getUser(Long.valueOf(processInstance.getStartUserId()));
//                    messageService.sendMessageWhenTaskAssigned(BpmTaskConvert.INSTANCE.convert(processInstance, startUser, task));
//                });
            }

        });
    }

    /**
     * 处理 Task 完成事件，目前是发送任务后置通知
     *
     * @param task 任务实体
     */
    @Override
    public void processTaskCompleted(Task task) {
        ProcessInstance processInstance = processInstanceService.getProcessInstance(task.getProcessInstanceId());
        if (processInstance == null) {
            log.error("[processTaskCompleted][taskId({}) 没有找到流程实例]", task.getId());
            return;
        }
        BpmProcessDefinitionInfoPO processDefinitionInfo = bpmProcessDefinitionService.
                getProcessDefinitionInfo(processInstance.getProcessDefinitionId());
        if (processDefinitionInfo == null) {
            log.error("[processTaskCompleted][processDefinitionId({}) 没有找到流程定义]", processInstance.getProcessDefinitionId());
            return;
        }

        // 任务后置通知
//        if (ObjUtil.isNotNull(processDefinitionInfo.getTaskAfterTriggerSetting())) {
//            BpmModelMetaInfoDTO.HttpRequestSetting setting = processDefinitionInfo.getTaskAfterTriggerSetting();
//            BpmHttpRequestUtils.executeBpmHttpRequest(processInstance,
//                    setting.getUrl(), setting.getHeader(), setting.getBody(), true, setting.getResponse());
//        }
    }

    /**
     * 处理 Task 审批超时事件，可能会处理多个当前审批中的任务
     *
     * @param processInstanceId 流程示例编号
     * @param taskDefineKey     任务 Key
     * @param handlerType       处理类型，参见 {@link}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processTaskTimeout(String processInstanceId, String taskDefineKey, Integer handlerType) {
        ProcessInstance processInstance = processInstanceService.getProcessInstance(processInstanceId);
        if (processInstance == null) {
            log.error("[processTaskTimeout][processInstanceId({}) 没有找到流程实例]", processInstanceId);
            return;
        }
        List<Task> taskList = getRunningTaskListByProcessInstanceId(processInstanceId, true, taskDefineKey);
        // TODO 优化：未来需要考虑加签的情况
        if (CollUtil.isEmpty(taskList)) {
            log.error("[processTaskTimeout][processInstanceId({}) 定义Key({}) 没有找到任务]", processInstanceId, taskDefineKey);
            return;
        }

        taskList.forEach(task -> {
            // 情况一：自动提醒
//            if (Objects.equals(handlerType, BpmUserTaskTimeoutHandlerTypeEnum.REMINDER.getType())) {
//                messageService.sendMessageWhenTaskTimeout(new BpmMessageSendWhenTaskTimeoutReqDTO()
//                        .setProcessInstanceId(processInstanceId).setProcessInstanceName(processInstance.getName())
//                        .setTaskId(task.getId()).setTaskName(task.getName()).setAssigneeUserId(Long.parseLong(task.getAssignee())));
//                return;
//            }

            // 情况二：自动同意
            if (Objects.equals(handlerType, BpmUserTaskTimeoutHandlerTypeEnum.APPROVE.getType())) {
                approveTask(Long.parseLong(task.getAssignee()),
                        new BpmTaskApproveDTO().setId(task.getId()).setReason(BpmReasonEnum.TIMEOUT_APPROVE.getReason()));
                return;
            }

            // 情况三：自动拒绝
            if (Objects.equals(handlerType, BpmUserTaskTimeoutHandlerTypeEnum.REJECT.getType())) {
                rejectTask(Long.parseLong(task.getAssignee()),
                        new BpmTaskRejectDTO().setId(task.getId()).setReason(BpmReasonEnum.REJECT_TASK.getReason()));
            }
        });
    }

    /**
     * 处理 ChildProcess 子流程的审批超时事件
     *
     * @param processInstanceId 流程示例编号
     * @param taskDefineKey     任务 Key
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processChildProcessTimeout(String processInstanceId, String taskDefineKey) {
        List<ActivityInstance> activityInstances = runtimeService.createActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .activityId(taskDefineKey).list();
        activityInstances.forEach(activityInstance -> moveTaskToEnd(activityInstance.getCalledProcessInstanceId(), BpmReasonEnum.TIMEOUT_APPROVE.getReason()));
    }

    /**
     * 触发流程任务 (ReceiveTask) 的执行
     * <p>
     * 1. Simple 模型 HTTP 回调请求触发器节点的回调，触发流程继续执行
     * 2. Simple 模型延迟器节点，到时触发流程继续执行
     *
     * @param processInstanceId 流程示例编号
     * @param taskDefineKey     任务 Key
     */
    @Override
    public void triggerTask(String processInstanceId, String taskDefineKey) {
        Execution execution = runtimeService.createExecutionQuery()
                .processInstanceId(processInstanceId)
                .activityId(taskDefineKey)
                .singleResult();
        if (execution == null) {
            log.error("[triggerTask][processInstanceId({}) activityId({}) 没有找到执行活动]", processInstanceId, taskDefineKey);
            return;
        }

        // 若存在直接触发接收任务，执行后续节点
        runtimeService.trigger(execution.getId());


    }

    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private BpmTaskServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }

    /**
     * WebSocket 待办消息推送工具方法
     *
     * @param task            当前任务
     * @param processInstance 流程实例
     * @param isCandidateOnly 是否只发给候选人（角色组）
     */
    private void pushWebSocketNotification(Task task, ProcessInstance processInstance, boolean isCandidateOnly) {
        try {
            // 1. 获取发起人姓名
            String startUserName = "系统";
            if (StrUtil.isNotBlank(processInstance.getStartUserId())) {
                SysUserDTO startUser = sysUserService.getById(NumberUtils.parseLong(processInstance.getStartUserId()));
                if (startUser != null) {
                    startUserName = startUser.getUserName();
                }
            }

            // 获取流程名称与任务名称组合
            String processName = StrUtil.isNotBlank(processInstance.getName()) ? processInstance.getName() : "业务审批";
            String taskFullName = processName + "-" + task.getName();

            // 2. 收集接收人账号
            Set<String> receiverAccounts = new HashSet<>();
            if (!isCandidateOnly && StrUtil.isNotBlank(task.getAssignee())) {
                SysUserDTO assigneeUser = sysUserService.getById(NumberUtils.parseLong(task.getAssignee()));
                if (assigneeUser != null && StrUtil.isNotBlank(assigneeUser.getUserAccount())) {
                    receiverAccounts.add(assigneeUser.getUserAccount());
                }
            } else if (StrUtil.isBlank(task.getAssignee())) {
                List<IdentityLink> links = taskService.getIdentityLinksForTask(task.getId());
                for (IdentityLink link : links) {
                    if ("candidate".equals(link.getType())) {
                        if (StrUtil.isNotBlank(link.getUserId())) {
                            SysUserDTO candidateUser = sysUserService.getById(NumberUtils.parseLong(link.getUserId()));
                            if (candidateUser != null && StrUtil.isNotBlank(candidateUser.getUserAccount())) {
                                receiverAccounts.add(candidateUser.getUserAccount());
                            }
                        } else if (StrUtil.isNotBlank(link.getGroupId())) {
                            List<SysUserDTO> roleUsers = sysRoleService.getUsersByRoleId(NumberUtils.parseLong(link.getGroupId()));
                            if (CollUtil.isNotEmpty(roleUsers)) {
                                for (SysUserDTO roleUser : roleUsers) {
                                    if (StrUtil.isNotBlank(roleUser.getUserAccount())) {
                                        receiverAccounts.add(roleUser.getUserAccount());
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. 执行 WebSocket 模板推送
            for (String account : receiverAccounts) {
                WebSocketUtils.sendTemplateNotification(
                        account,
                        BpmMessageConstants.TASK_CREATED,
                        taskFullName,
                        startUserName
                );
                log.info("[WebSocket推送] 待办任务已推送给账号: {}", account);
            }

        } catch (Exception e) {
            log.error("[WebSocket推送] 待办任务消息发送失败, taskId: {}", task.getId(), e);
        }
    }

}
