package com.yy.framework.flowable.listener.exection;

import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * 类型为 delegateExpression 的 ExecutionListener 监听器示例
 * 执行监听器具体例子
 */
@Component
@Slf4j
public class DemoDelegateExpressionExecutionListener implements JavaDelegate {
    /**
     * 参数值，version即为字段名称，必须一致
     */
    private Expression version;

    @Override
    public void execute(DelegateExecution execution) {
        log.info("[execute][execution({}) 被调用！变量有：{}]", execution.getId(),
                execution.getCurrentFlowableListener().getFieldExtensions());

        // 获取字段值
        Object versionValue = version.getValue(execution);

        // 1. 获取流程实例 ID (ACT_RU_EXECUTION 表的 PROC_INST_ID_)
        String processInstanceId = execution.getProcessInstanceId();

        // 2. 获取业务主键 (BusinessKey)
        // 这是最常用的，通过它可以关联到你自己的业务表（如请假单 ID、订单 ID）
        String businessKey = execution.getProcessInstanceBusinessKey();

        // 3. 获取流程定义 ID (ProcDefId)
        String processDefinitionId = execution.getProcessDefinitionId();

        // 4. 获取当前节点（活动）的 ID 和 名称
        String activityId = execution.getCurrentActivityId();

    }

}
