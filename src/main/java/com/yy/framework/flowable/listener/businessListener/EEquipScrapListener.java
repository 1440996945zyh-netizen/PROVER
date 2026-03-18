package com.yy.framework.flowable.listener.businessListener;

import com.yy.common.flowable.constants.BpmnVariableConstants;
import com.yy.ppm.equipment.bean.po.EEquipScrapPO;
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
 * 设备报废流程监听器
 */
@Component("EEquipScrapListener")
@Slf4j
public class EEquipScrapListener implements JavaDelegate {
    @Autowired
    private EEquipScrapService eEquipScrapService;

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
        Long businessDataId = eEquipScrapService.getBusinessDataIdByProcessInstanceId(processInstanceId);

        // 4. 判断流程最终通过/拒绝状态
        Integer status = (Integer) execution.getVariable(BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_STATUS);

        // Status = 3 代表驳回/拒绝
        if (status != null && status == 3) {
            log.info("流程审批被拒绝 (Status:3)，修改结算子单为拒绝状态...");
            Date now = new Date();

            EEquipScrapPO updatePO = new EEquipScrapPO();
            updatePO.setId(businessDataId);
            updatePO.setStatus(3L);
            updatePO.setExecuteFulfilTime(now);
            updatePO.setUpdateTime(now);
            int count = eEquipScrapService.updateStatus(updatePO);
        } else {
            log.info("流程审批通过 (Status:{})，更新设备报废单状态为已报废...", status);
            // 审批通过逻辑,更新设备报废单状态为已报废
            eEquipScrapService.confirm(businessDataId, "");
        }
    }

}
