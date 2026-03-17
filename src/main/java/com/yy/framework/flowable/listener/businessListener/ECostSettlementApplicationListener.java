package com.yy.framework.flowable.listener.businessListener;

import com.yy.common.flowable.constants.BpmnVariableConstants;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.ECostSettlementApplyDTO;
import com.yy.ppm.equipment.bean.dto.ECostSettlementApplySubDTO;
import com.yy.ppm.equipment.service.ECostSettlementApplyService;
import com.yy.ppm.equipment.service.EMaintProjApplyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 类型为 delegateExpression 的 ExecutionListener 监听器示例
 * 结算申请流程监听器
 */
@Component("ECostSettlementApplicationListener")
@Slf4j
public class ECostSettlementApplicationListener implements JavaDelegate {
    @Autowired
    private ECostSettlementApplyService eCostSettlementApplyService;
    @Autowired
    private EMaintProjApplyService eMaintProjApplyService;


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
        Long businessDataId = eCostSettlementApplyService.getBusinessDataIdByProcessInstanceId(processInstanceId);

        // 3. 获取业务数据
        ECostSettlementApplyDTO eCostSettlementApplyDTO = eCostSettlementApplyService.getById(businessDataId);
        if (eCostSettlementApplyDTO == null || CollectionUtils.isEmpty(eCostSettlementApplyDTO.getSubList())) {
            throw new BusinessRuntimeException("该流程关联的结算申请数据（或子表数据）不存在");
        }

        // 4. 判断流程最终通过/拒绝状态
        Integer status = (Integer) execution.getVariable(BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_STATUS);

        // Status = 3 代表驳回/拒绝
        if (status != null && status == 3) {
            log.info("流程审批被拒绝 (Status:3)，修改结算子单为拒绝状态...");
            eCostSettlementApplyService.updateRejectStatusByApplyId(businessDataId, "1");
        } else {
            log.info("流程审批通过 (Status:{})，开始更新关联单据状态...", status);
            // 审批通过逻辑：更新维修项目申请单的结算状态为“已结算”
            for (ECostSettlementApplySubDTO applySubDTO : eCostSettlementApplyDTO.getSubList()) {
                eMaintProjApplyService.updateIsSettlement(applySubDTO.getMantAppNumber(), "1");
            }
        }
    }

}
