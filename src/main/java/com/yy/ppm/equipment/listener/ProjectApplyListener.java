package com.yy.ppm.equipment.listener;

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
@Component("ProjectApplyListener")
@Slf4j
public class ProjectApplyListener implements TaskListener {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private RuntimeService runtimeService;


    @Override
    public void notify(DelegateTask delegateTask) {

        System.out.println("測試监听器");
    }
}