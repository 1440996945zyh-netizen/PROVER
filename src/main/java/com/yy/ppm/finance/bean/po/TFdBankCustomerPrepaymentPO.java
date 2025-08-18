package com.yy.ppm.finance.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 客户预缴(TFdBankCustomerPrepayment)PO
 * @Description
 * @createTime 2023年09月14日 10:30:00
 */
@Data
public class TFdBankCustomerPrepaymentPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 724160769298986203L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 预缴编号
     */
    private String prepaymentCode;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 公司id
     */
    private Long companyId;
    /**
     * 预缴类型code 1预缴 2 补缴
     */
    private Long prepaymentTypeCode;
    /**
     * 预缴类型名称
     */
    private String prepaymentTypeName;
    /**
     * 客户ID
     */
    private Long customerId;
    /**
     * 客户名称
     */
    private String customerName;
    /**
     * 预缴日期(默认不可更改)
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date prepaymentTime;
    /**
     * 预缴金额
     */
    private BigDecimal prepaymentAmount;
    /**
     * 已用金额
     */
    private BigDecimal utilizedAmount;
    /**
     * 付款方式
     */
    private Long paymentMethodCode;
    /**
     * 付款方式名称
     */
    private String paymentMethodName;
    /**
     * 银行名称
     */
    private String bankName;
    /**
     * 银行ID
     */
    private Long bankId;
    /**
     * 状态 1正常 2作废
     */
    private Long status;
    /**
     * 备注
     */
    private String remark;

    /**
     * 作废备注
     */
    private String voidRemark;

    /**
     * 收据主表Id
     */
    private Long debtorpaymentId;
    /**
     * 原收据id     SOURCE_DEBTORPAYMENT_ID
     */
    private Long sourceDebtorpaymentId;


    /***
     * 作业通知单编号
     */
    private String trustNo;

    /**
     * 作业通知单主表iD BUS_TRUST_ID
     */
    private Long busTrustId;
    /**
     * 票货表ID BUS_TRUST_CARGO_ID
     */
    private Long busTrustCargoId;

    /**
     *  预缴方式   预缴方式 PREPAY_MODE_CODE   PREPAY_MODE_NAME
     */
    private String prepayModeCode;
    private String prepayModeName;

    /**
     * SOURCE_PREPAY_CODE
     * 货物预缴专用得，选中的预缴编号
     */
    private String sourcePrepayCode;

    /**
     * 票货ID
     */
    private Long cargoInfoId;

    private Long voidBy;

    /**
     * 录入人姓名
     */
    private String voidByName;

    /**
     * 录入时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date voidTime;
}

