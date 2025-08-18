package com.yy.ppm.finance.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 收据主表(TFdDebtorpaymenDetail)SearchDTO
 * @Description TODO
 * @createTime 2023年09月20日 11:44:00
 */
@Data
public class TFdDebtorpaymenDetailSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -25830981876444012L;

    /***/
    private Long id;
    /**
     * 收据主表ID
     */
    private Long debtorpaymentId;
    /**
     * 序号
     */
    private Long serialNumber;
    /**
     * 发票ID (预缴ID )
     */
    private Long invoicePrepayId;
    /**
     * 发票编号 (预缴编号)
     */
    private Long invoicePrepayNo;
    /**
     * 系统发票号码(补缴时值为 1 )
     */
    private Long sysInvoicePrepayNo;
    /**
     * 状态
     */
    private Long status;
    /**
     * 发票或者预缴的金额
     */
    private Long invoicePrepayAmount;
    /**
     * 收据付款类型(10 预缴 20 补缴 30 退还)
     */
    private Long debtorpayPaymentTypeCode;
    /**
     * 收据付款类型名称
     */
    private String debtorpayPaymentTypeName;
    /**
     * 付款类型（字典 BANK_PAY_METHOD  （1：借记卡、2：贷记卡、3：微信、4：支票、5：银行电汇、6：支付宝、7：现金、8：银行承兑））
     */
    private Long paymentMethodCode;
    /**
     * 付款类型
     */
    private String paymentMethodName;
    /**
     * 付款文件号码
     */
    private Long fileNo;
    /**
     * 付款文件时间
     */
    private Date fileTime;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建者-姓名
     */
    private String createByName;
    /**
     * 更新者-姓名
     */
    private String updateByName;
}

