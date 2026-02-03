package com.yy.ppm.flowable.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.ObjectUtil;
import com.yy.common.flowable.constants.ErrorCodeConstants;
import com.yy.common.page.Pages;
import com.yy.ppm.flowable.bean.dto.BpmProcessInstanceCopySearchDTO;
import com.yy.ppm.flowable.bean.po.BpmProcessInstanceCopyPO;
import com.yy.ppm.flowable.mapper.BpmProcessInstanceCopyMapper;
import com.yy.ppm.flowable.service.BpmProcessDefinitionService;
import com.yy.ppm.flowable.service.BpmProcessInstanceCopyService;
import com.yy.ppm.flowable.service.BpmProcessInstanceService;
import com.yy.ppm.flowable.service.BpmTaskService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.util.Collection;
import java.util.List;

import static com.yy.common.flowable.utils.CollectionUtils.convertList;
import static com.yy.common.flowable.utils.ServiceExceptionUtil.exception;

/**
 * 流程抄送 Service 实现类
 *
 * @author kyle
 */
@Service
@Validated
@Slf4j
public class BpmProcessInstanceCopyServiceImpl implements BpmProcessInstanceCopyService {

    @Resource
    private BpmProcessInstanceCopyMapper processInstanceCopyMapper;

    @Resource
    @Lazy // 延迟加载，避免循环依赖
    private BpmTaskService taskService;

    @Resource
    @Lazy // 延迟加载，避免循环依赖
    private BpmProcessInstanceService processInstanceService;
    @Resource
    @Lazy // 延迟加载，避免循环依赖
    private BpmProcessDefinitionService processDefinitionService;
    @Autowired
    private Snowflake snowflake;

    /**
     * 【管理员】流程实例的抄送
     *
     * @param userIds 抄送的用户编号
     * @param reason 抄送意见
     * @param taskId 流程任务编号
     */
    @Override
    public void createProcessInstanceCopy(Collection<Long> userIds, String reason, String taskId) {
        Task task = taskService.getTask(taskId);
        if (ObjectUtil.isNull(task)) {
            throw exception(ErrorCodeConstants.TASK_NOT_EXISTS);
        }
        // 执行抄送
        createProcessInstanceCopy(userIds, reason,
                task.getProcessInstanceId(), task.getTaskDefinitionKey(), task.getName(), task.getId());
    }


    /**
     * 【自动抄送】流程实例的抄送执行
     *
     * @param userIds 抄送的用户编号
     * @param reason 抄送意见
     * @param processInstanceId 流程编号
     * @param activityId 流程活动编号（对应 {@link FlowNode#getId()}）
     * @param activityName 任务编号（对应 {@link FlowNode#getName()}）
     * @param taskId 任务编号，允许空
     */
    @Override
    public void createProcessInstanceCopy(Collection<Long> userIds, String reason, String processInstanceId,
                                          String activityId, String activityName, String taskId) {
        // 1.1 校验流程实例存在
        ProcessInstance processInstance = processInstanceService.getProcessInstance(processInstanceId);
        if (processInstance == null) {
            throw exception(ErrorCodeConstants.PROCESS_INSTANCE_NOT_EXISTS);
        }
        // 1.2 校验流程定义存在
        ProcessDefinition processDefinition = processDefinitionService.getProcessDefinition(
                processInstance.getProcessDefinitionId());
        if (processDefinition == null) {
            throw exception(ErrorCodeConstants.PROCESS_DEFINITION_NOT_EXISTS);
        }

        // 2. 创建抄送流程
        List<BpmProcessInstanceCopyPO> copyList = convertList(userIds, userId -> new BpmProcessInstanceCopyPO()
                .setId(snowflake.nextId())
                .setUserId(userId).setReason(reason).setStartUserId(Long.valueOf(processInstance.getStartUserId()))
                .setProcessInstanceId(processInstanceId).setProcessInstanceName(processInstance.getName())
                .setCategory(processDefinition.getCategory()).setTaskId(taskId)
                .setActivityId(activityId).setActivityName(activityName)
                .setProcessDefinitionId(processInstance.getProcessDefinitionId()));
        processInstanceCopyMapper.insertBatch(copyList);
    }

    /**
     * 获得抄送的流程的分页
     *
     * @param pageReqVO 分页请求
     * @return 抄送的分页结果
     */
    @Override
    public Pages<BpmProcessInstanceCopyPO> getProcessInstanceCopyPage(BpmProcessInstanceCopySearchDTO pageReqVO) {
        return processInstanceCopyMapper.selectPage(pageReqVO);
    }

    /**
     * 删除抄送流程
     *
     * @param processInstanceId 流程实例 ID
     */
    @Override
    public void deleteProcessInstanceCopy(String processInstanceId) {
        processInstanceCopyMapper.deleteByProcessInstanceId(processInstanceId);
    }

}
