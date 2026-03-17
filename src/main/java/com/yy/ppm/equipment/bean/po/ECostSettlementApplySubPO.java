package com.yy.ppm.equipment.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 结算申请子表 PO
 *
 * @author fanxianjin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ECostSettlementApplySubPO extends BasePO {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 结算申请表主表ID */
    private Long applyId;

    /** 工单号 */
    private String workOrderNo;

    /** 设备ID */
    private Long equipId;

    /** 维修项目申请单号 */
    private String mantAppNumber;

    /** 工单验收时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date acceptanceTime;

    /** 预算金额(维修项目申请单预算金额，元，含税) */
    private BigDecimal budgetAmount;

    /** 实际金额(元，含税) */
    private BigDecimal actualAmount;

    /** 是否审批拒绝（0否/1是） */
    private String isApprovalReject;
}
