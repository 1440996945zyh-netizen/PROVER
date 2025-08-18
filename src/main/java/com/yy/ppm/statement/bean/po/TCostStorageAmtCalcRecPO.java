package com.yy.ppm.statement.bean.po;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 堆存费金额计算记录表(TCostStorageAmtCalcRec)PO
 *
 * @author linqi
 * @since 2024-04-07 16:56:31
 */
@Setter
@Getter
public class TCostStorageAmtCalcRecPO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 票货ID
     */
    private Long cargoInfoId;

    /**
     * 超期天数
     */
    private Integer overdueDays;

    /**
     * 是否结算 0否/1是
     */
    private String isSettlement;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 失败编码
     */
    private String failedCode;

    /**
     * 失败备注
     */
    private String failedRemark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 本次计费开始时间   CALC_START_TIME
     */
    private Date calcStartTime;
    /**
     * 本次计算终止时间  CALC_END_TIME
      */
    private Date calcEndTime;

    /**
     * 剩余免堆存期
     */
    private Integer freeDays;
}
