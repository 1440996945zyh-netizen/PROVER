package com.yy.ppm.finance.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 收据主表(TFdDebtorpaymenDetail)PO
 * @Description
 * @createTime 2023年09月20日 11:44:00
 */
@Data
public class TFdDebtorpaymentDetailPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 330325766808311059L;

    /**
     *
     */
    private Long id;
    /**
     * 收据主表ID
     */
    private Long debtorpaymentId;
    /**
     * 序号
     */
    private Integer serialNumber;
    /**
     * 发票ID (预缴ID )
     */
    private Long invoicePrepayId;
    /**
     * 发票编号 (预缴编号)
     */
    private String invoicePrepayNo;
    /**
     * 系统发票号码(补缴时值为 1 )
     */
    private String sysInvoicePrepayNo;
    /**
     * 类型（1：发票，2：预缴
     */
    private Long type;
    /**
     * 发票或者预缴的金额
     */
    private BigDecimal invoicePrepayAmount;
    /**
     * 收据付款类型(10 预缴 20 补缴 30 退还)
     */
    private Long debtorpayPaymentTypeCode;
    /**
     * 收据付款类型名称
     */
    private String debtorpayPaymentTypeName;
    /**
     * 付款方式（字典 BANK_PAY_METHOD  （1：借记卡、2：贷记卡、3：微信、4：支票、5：银行电汇、6：支付宝、7：现金、8：银行承兑））
     */
    private Long paymentMethodCode;
    /**
     * 付款方式
     */
    private String paymentMethodName;
    /**
     * 付款文件号码
     */
    private String fileNo;
    /**
     * 付款文件时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonIgnore
    private Date fileTime;
    /**
     * 备注
     */
    private String remark;


    /***
     * 创建时间
     */
    private String createTimeNew;


}

