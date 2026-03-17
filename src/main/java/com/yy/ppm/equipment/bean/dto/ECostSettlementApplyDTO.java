package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.equipment.bean.po.ECostSettlementApplyPO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 结算申请 DTO
 *
 * @author antigravity
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ECostSettlementApplyDTO extends ECostSettlementApplyPO {

    private static final long serialVersionUID = 1L;

    /**
     * 子表列表
     */
    private List<ECostSettlementApplySubDTO> subList;

    /**
     * 流程状态
     */
    private String processStatus;
    /**
     * 流程状态标签
     */
    private String processStatusLabel;
    /**
     * 流程实例ID
     */
    private String procInstId;

    /** 工单号（逗号隔开） */
    private String workOrderNos;
}
