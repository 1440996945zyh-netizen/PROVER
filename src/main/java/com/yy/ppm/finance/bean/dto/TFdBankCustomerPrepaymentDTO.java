package com.yy.ppm.finance.bean.dto;


import com.yy.ppm.finance.bean.po.TFdBankCustomerPrepaymentPO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 客户预缴(TFdBankCustomerPrepayment)DTO
 * @Description
 * @createTime 2023年09月14日 10:30:00
 */
@Data
public class TFdBankCustomerPrepaymentDTO extends TFdBankCustomerPrepaymentPO {

    private static final long serialVersionUID = -71773950601194395L;
    /**
     * 总预缴款（大预缴）
     */
    private BigDecimal allPrepaymentAmount;
    /**
     * 实际剩余金额
     */
    private BigDecimal residualAmount;
    /**
     * 预估剩余金额
     */
    private BigDecimal estiResidualAmount;
    /**
     * 船舶押金
     */
    private BigDecimal shipDeposit;
    /**
     * 货物预缴
     */
    private BigDecimal cargoDeposit;
    /**
     * 剩余预缴款
     */
    private BigDecimal residualPreAmount;
    /**
     * 发票金额
     */
    private BigDecimal invoiceAmount;

    /** 附件 */
    private List<Long> fileIds;

    /**
     * 通知单显示label
     */
    private String trustLabel;

    /**
     * 回显船名航次
     */
    private String shipVoyageName;
    /**
     * ShipVoyageIds
     */
    private String shipVoyageIds;


    private Long prePayId;
    private String prePayCode;
    private String prePayCodeLabel;

    private BigDecimal tmpAmount;

    private String transSequenceIdn;

//      剩余可用金额
    private BigDecimal realResidueAmount;
//      未作付款收据的发票金额
    private BigDecimal noReceiptFinance;
    //剩余货物预缴
    private BigDecimal cargoDepositCan;

}
