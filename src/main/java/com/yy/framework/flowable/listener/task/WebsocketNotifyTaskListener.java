package com.yy.framework.flowable.listener.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.yy.common.flowable.constants.BpmMessageConstants;
import com.yy.common.ws.WebSocketUtils;
import com.yy.ppm.flowable.service.BpmProcessInstanceService;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import com.yy.ppm.system.service.SysRoleService;
import com.yy.ppm.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 【局部消息推送】自定义任务监听器
 * 
 * 作用：在流程设计器中给重要节点的 UserTask 挂载该监听器。
 * 效果：无论系统全局 WebSocket 推送开关是否关闭，只要挂了本监听器，就一定会强行推送待办消息。
 * 
 * 配置类型：代理表达式 (delegateExpression)
 * 配置值：${WebsocketNotifyTaskListener}
 * 事件类型：
 *   - create (推荐：适用于角色或自动分配)
 *   - assignment (推荐：适用于后续流转时的任务签收或分配)
 */
@Component("WebsocketNotifyTaskListener")
@Slf4j
public class WebsocketNotifyTaskListener implements TaskListener {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private RuntimeService runtimeService;


    @Override
    public void notify(DelegateTask delegateTask) {
        // 如果业务代码已经发过特定的(转办/委派/退回)通知，自定义监听器就不发了
        Boolean skipNotify = (Boolean) delegateTask.getVariableLocal("SKIP_DEFAULT_NOTIFY");
        if (skipNotify != null && skipNotify) {
            log.info("[WebsocketNotifyTaskListener] 发现省略标记，跳过通用待办发送。taskId: {}", delegateTask.getId());
            return;
        }

        log.info("[WebsocketNotifyTaskListener] 触发局部强推监听器, 任务ID: {}, 事件: {}", 
                 delegateTask.getId(), delegateTask.getEventName());

        try {
            // 1. 获取流程名称与发起人信息
            String processInstanceId = delegateTask.getProcessInstanceId();
            String processName = "业务审批";
            String startUserName = "系统";
            ProcessInstance processInstance =
                    runtimeService.createProcessInstanceQuery()
                            .processInstanceId(processInstanceId)
                            .singleResult();
            if (processInstance != null && StrUtil.isNotBlank(processInstance.getName())) {
                processName = processInstance.getName();
            }

            String taskFullName = processName + "-" + delegateTask.getName();
            Long startUserId = (Long) runtimeService.getVariable(processInstanceId, "PROCESS_START_USER_ID");
            if (startUserId != null) {
                SysUserDTO startUser = sysUserService.getById(startUserId);
                if (startUser != null) {
                    startUserName = startUser.getUserName();
                }
            }

            Set<String> receiverAccounts = new HashSet<>();

            // 2. 根据事件类型收集接收人
            if (EVENTNAME_CREATE.equals(delegateTask.getEventName()) && StrUtil.isBlank(delegateTask.getAssignee())) {
                // Create 事件且无 assignee -> 找角色组
                Set<IdentityLink> candidates = delegateTask.getCandidates();
                for (IdentityLink link : candidates) {
                    if (StrUtil.isNotBlank(link.getUserId())) {
                        SysUserDTO user = sysUserService.getById(Long.parseLong(link.getUserId()));
                        if (user != null && StrUtil.isNotBlank(user.getUserAccount())) {
                            receiverAccounts.add(user.getUserAccount());
                        }
                    } else if (StrUtil.isNotBlank(link.getGroupId())) {
                        List<SysUserDTO> roleUsers = sysRoleService.getUsersByRoleId(Long.parseLong(link.getGroupId()));
                        if (CollUtil.isNotEmpty(roleUsers)) {
                            for (SysUserDTO roleUser : roleUsers) {
                                if (StrUtil.isNotBlank(roleUser.getUserAccount())) {
                                    receiverAccounts.add(roleUser.getUserAccount());
                                }
                            }
                        }
                    }
                }
            } else if (EVENTNAME_ASSIGNMENT.equals(delegateTask.getEventName()) && StrUtil.isNotBlank(delegateTask.getAssignee())) {
                // Assignment 事件 -> 找具体人
                SysUserDTO assigneeUser = sysUserService.getById(Long.parseLong(delegateTask.getAssignee()));
                if (assigneeUser != null && StrUtil.isNotBlank(assigneeUser.getUserAccount())) {
                    receiverAccounts.add(assigneeUser.getUserAccount());
                }
            } else {
                return; // 不符合的事件直接跳过
            }

            // 3. 执行推送
            for (String account : receiverAccounts) {
                Object[] args = new Object[]{taskFullName, startUserName};
                WebSocketUtils.sendTemplateNotification(
                        account,
                        BpmMessageConstants.TASK_CREATED,
                        true,
                        args
                );
                log.info("[WebSocket推送] 局部监听器强制下发成功, 账号: {}", account);
            }

            // 给任务打上省略标记-避免全局重复推送
            delegateTask.setVariableLocal("SKIP_DEFAULT_NOTIFY", true);

        } catch (Exception e) {
            log.error("[WebSocket推送] 局部监听器强制下发失败, taskId: {}", delegateTask.getId(), e);
        }
    }
}