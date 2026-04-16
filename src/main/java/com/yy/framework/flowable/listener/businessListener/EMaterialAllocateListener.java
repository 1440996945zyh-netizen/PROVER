package com.yy.framework.flowable.listener.businessListener;

import com.yy.common.flowable.constants.BpmnVariableConstants;
import com.yy.ppm.equipment.service.EMaterialAllocateService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 物资调拨流程监听器
 * @author system
 */
@Component("EMaterialAllocateListener")
@Slf4j
public class EMaterialAllocateListener implements JavaDelegate {

    @Resource
    private EMaterialAllocateService materialAllocateService;

    private Expression version;

    /**
     * 流程结束时触发。
     * 驳回则回写状态，审批通过则自动执行调拨。
     */
    @Override
    public void execute(DelegateExecution execution) {
        if (version != null) {
            version.getValue(execution);
        }

        // 通过流程实例ID反查业务主键
        String processInstanceId = execution.getProcessInstanceId();
        Long businessDataId = materialAllocateService.getBusinessDataIdByProcessInstanceId(processInstanceId);
        Integer status = (Integer) execution.getVariable(BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_STATUS);

        if (businessDataId == null) {
            log.warn("物资调拨未找到业务ID, processInstanceId={}", processInstanceId);
            return;
        }

        if (status != null && status == 3) {
            materialAllocateService.updateExecuteResult(businessDataId, 0, "审批驳回", null, null, null, null);
            return;
        }

        try {
            // 审批通过后执行真正的库存调拨
            materialAllocateService.executeAllocate(businessDataId);
        } catch (Exception e) {
            log.error("物资调拨执行失败, businessDataId={}", businessDataId, e);
            materialAllocateService.updateExecuteResult(businessDataId, 2, null, null, null, null, null);
            throw e;
        }
    }
}
