package com.yy.ppm.flowable.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.yy.common.enums.WebsocketEnum;
import com.yy.common.flowable.constants.BpmMessageConstants;
import com.yy.common.flowable.constants.ErrorCodeConstants;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.ws.WebSocketUtils;
import com.yy.ppm.flowable.bean.dto.BpmProcessInstanceCopySearchDTO;
import com.yy.ppm.flowable.bean.po.BpmFormPO;
import com.yy.ppm.flowable.bean.po.BpmProcessInstanceCopyPO;
import com.yy.ppm.flowable.mapper.BpmProcessInstanceCopyMapper;
import com.yy.ppm.flowable.service.BpmProcessDefinitionService;
import com.yy.ppm.flowable.service.BpmProcessInstanceCopyService;
import com.yy.ppm.flowable.service.BpmProcessInstanceService;
import com.yy.ppm.flowable.service.BpmTaskService;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import com.yy.ppm.system.service.SysUserService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final SysUserService sysUserService;

    private final Snowflake snowflake;

    public BpmProcessInstanceCopyServiceImpl(
            SysUserService sysUserService,
            Snowflake snowflake
    ){
        this.sysUserService = sysUserService;
        this.snowflake = snowflake;
    }


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

        // 3. 发送 WebSocket 抄送通知
        pushWebSocketCopyNotification(userIds, processInstance, activityName, reason);
    }

    /**
     * 发送 WebSocket 抄送通知
     */
    private void pushWebSocketCopyNotification(Collection<Long> userIds, ProcessInstance processInstance,
                                               String activityName, String reason) {
        if (CollUtil.isEmpty(userIds)) {
            return;
        }

        try {
            // 获取操作人/发起人姓名
            String startUserName = "系统";
            if (StrUtil.isNotBlank(processInstance.getStartUserId())) {
                SysUserDTO startUser = sysUserService.getById(Long.valueOf(processInstance.getStartUserId()));
                if (startUser != null) {
                    startUserName = startUser.getUserName();
                }
            }

            // 批量查询并执行推送
            List<SysUserDTO> copyUsers = sysUserService.getUserList(userIds);
            if (CollUtil.isNotEmpty(copyUsers)) {
                for (SysUserDTO user : copyUsers) {
                    if (StrUtil.isNotBlank(user.getUserAccount())) {
                        WebSocketUtils.sendTemplateNotification(
                                user.getUserAccount(),
                                BpmMessageConstants.TASK_COPY,
                                startUserName
                        );
                        log.info("[WebSocket推送] 抄送通知已推送给账号: {}", user.getUserAccount());
                    }
                }
            }
        } catch (Exception e) {
            log.error("[WebSocket推送] 抄送通知发送失败, processInstanceId: {}", processInstance.getId(), e);
        }
    }

    /**
     * 获得抄送的流程的分页
     *
     * @param pageReqVO 分页请求
     * @return 抄送的分页结果
     */
    @Override
    public Pages<BpmProcessInstanceCopyPO> getProcessInstanceCopyPage(BpmProcessInstanceCopySearchDTO pageReqVO) {
        Pages<BpmProcessInstanceCopyPO> pages = PageHelperUtils.limit(pageReqVO, () -> {
            return processInstanceCopyMapper.selectPage(pageReqVO);
        });
        return pages;
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
