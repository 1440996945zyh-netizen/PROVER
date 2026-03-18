package com.yy.framework.flowable.listener.businessListener;

import com.yy.common.flowable.constants.BpmnVariableConstants;
import com.yy.ppm.equipment.bean.po.EEquipAllocatePO;
import com.yy.ppm.equipment.bean.po.EEquipScrapPO;
import com.yy.ppm.equipment.service.EEquipAllocateService;
import com.yy.ppm.equipment.service.EEquipScrapService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 类型为 delegateExpression 的 ExecutionListener 监听器示例
 * 设备调拨流程监听器
 */
@Component("EEquipAllocateListener")
@Slf4j
public class EEquipAllocateListener implements JavaDelegate {
    @Autowired
    private EEquipAllocateService eEquipAllocateService;


    /**
     * 参数值，version即为字段名称，必须一致
     */
    private Expression version;

    @Override
    public void execute(DelegateExecution execution) {
        log.info("[execute][execution({}) 被调用！变量有：{}]", execution.getId(),
                execution.getCurrentFlowableListener().getFieldExtensions());

        // 1. 处理版本表达式 (保留原逻辑)
        if (version != null) {
            version.getValue(execution);
        }

        // 2. 获取流程关键 ID
        String processInstanceId = execution.getProcessInstanceId();
        Long businessDataId = eEquipAllocateService.getBusinessDataIdByProcessInstanceId(processInstanceId);

        // 4. 判断流程最终通过/拒绝状态
        Integer status = (Integer) execution.getVariable(BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_STATUS);

        // Status = 3 代表驳回/拒绝
        if (status != null && status == 3) {
            log.info("流程审批被拒绝 (Status:3)，更新设备调拨单状态为已驳回...");
            Date now = new Date();

            EEquipAllocatePO updatePO = new EEquipAllocatePO();
            updatePO.setId(businessDataId);
            updatePO.setStatus(3L);
            updatePO.setAllocateFulfilTime(now);
            updatePO.setUpdateTime(now);
            int count = eEquipAllocateService.updateStatus(updatePO);

        } else {
            log.info("流程审批通过 (Status:{})，更新设备调拨单状态为已完成...", status);
            // 审批通过逻辑,更新设备调拨单状态为已完成
            eEquipAllocateService.confirm(businessDataId, "");
        }
    }

}
