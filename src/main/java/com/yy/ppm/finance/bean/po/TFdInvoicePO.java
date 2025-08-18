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
 * @ClassName 发票表(TFdInvoice)PO
 * @Description
 * @createTime 2023年09月15日 20:22:00
 */
@Data
public class TFdInvoicePO extends BasePO implements Serializable {

    private static final long serialVersionUID = -89277978637180106L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 作业公司
     */
    private String companyName;
    /**
     * 作业公司id
     */
    private Long companyId;
    /**
     * 系统发票编号
     */
    private String sysInvoiceCode;
    /**
     * 税务服务发票编号
     */
    private String invoiceCode;
    /**
     * 付款人(客户名称
     */
    private Long customerId;
    /**
     * 付款人name(客户名称name
     */
    private String customerName;
    /**
     * 金额
     */
    private BigDecimal invoiceAmount;
    /**
     * 状态
     */
    private Long status;
    /**
     * 发票类型
     */
    private Long invoiceTypeCode;
    /**
     * 发票类型name
     */
    private String invoiceTypeName;
    /**
     * 税务服务发票（字典TAX_INVOICE
     */
    private String taxationInvoice;
    /**
     * 税务服务发票字典
     */
    private Long taxationInvoiceCode;
    /**
     * 开票日期
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date invoiceTime;
    /**
     * 备注
     */
    private String remark;

    /**
     * 结算单编号
     */
    private String statementNo;

    /***
     *
     */
    private Long debtorpaymentId;

    /**
     * 预缴类型  10：货方  30:船方    40:杂项
     */

    private Long prepaymentTypeCode;
    /**
     * 预缴类型
     */
    private String prepaymentTypeName;

    /**
     * 发票抬头
     */
    private String invoice;

    /**
     * 未作付款收据金额
     */
    private BigDecimal hasNotReceiptAmount;
}

