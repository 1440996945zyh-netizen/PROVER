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
 * @ClassName 贷方解放票据主表(TFdCreditDebitBill)PO
 * @Description
 * @createTime 2023年10月08日 16:19:00
 */
@Data
public class TFdCreditDebitBillPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 985600103648891141L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 系统票据编号
     */
    private String cndnCode;

    /**
     * 公司id
     */
    private Long companyId;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 税务发票类型Id
     */
    private Long taxationInvoiceCode;
    /**
     * 税务发票类型Name
     */
    private String taxationInvoice;
    /**
     * 贷方票据类型Code
     */
    private Long cndnBillTypeCode;
    /**
     * 贷方票据类型Name
     */
    private String cndnBillTypeName;
    /**
     * 发票号码
     */
    private String invoiceCode;
    /**
     * 付款人id
     */
    private Long customerId;
    /**
     * 付款人NAME
     */
    private String customerName;
    /**
     * 系统发票号码（VI/CI/MI）
     */
    private String sysInvoiceCode;
    /**
     * 冲销金额
     */
    private BigDecimal eliminateAmount;
    /**
     * 备注
     */
    private String remark;
    /**
     * CNDN类型   1是贷方 2是借方
     */
    private Long cndnType;
    /**
     * 是否作废
     */
    private Long status;
    /**
     * 作废原因
     */
    private String voidReason;
    /**
     * 作废备注
     */
    private String voidRemark;
    /**
     * 是否付款收据
     */
    private Long isDebtorPayment;

    /**
     *
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date cndnTime;

    /**
     * 收据主表id
     */
    private Long debtorpaymentId;

    /**
     * 发票抬头
     */
    private String invoice;


    /**
     * 未作付款收据的金额
     */
    private BigDecimal hasNotReceiptAmount;
    /**
     * 已做付款收据的金额
     */
    private BigDecimal hasReceiptAmount;
    /**
     * 付款收据状态  10 未作 30 已做
     */
    private String receiptStatus;
}

