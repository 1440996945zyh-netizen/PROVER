package com.yy.framework.flowable.listener.task;

import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

/**
 * 类型为 delegateExpression 的 TaskListener 监听器示例
 * 任务监听器具体例子
 */
@Component("DemoDelegateExpressionTaskListener")
@Slf4j
public class DemoDelegateExpressionTaskListener implements TaskListener {

    /**
     * 参数值，version即为字段名称，必须一致
     */
    private Expression version;

    @Override
    public void notify(DelegateTask delegateTask) {
        log.info("[execute][task({}) 被调用]", delegateTask.getId());

        if (version != null) {
            // 获取字段值
            Object versionValue = version.getValue(delegateTask);

        } else {
            // 打印日志或者给个默认值
            log.warn("version expression is null, skipping...");
        }

        // 1. 获取任务 ID (就是数据库 ACT_RU_TASK 表的主键)
        String taskId = delegateTask.getId();

        // 2. 获取任务定义 Key (设计器里配置的 ID，如 UserTask_1)
        String taskDefinitionKey = delegateTask.getTaskDefinitionKey();

        // 3. 获取任务名称
        String taskName = delegateTask.getName();

        // 4. 获取当前任务的负责人 (Assignee)
        String assignee = delegateTask.getAssignee();

        // 5. 获取流程实例 ID
        String processInstanceId = delegateTask.getProcessInstanceId();
    }


}
