package com.yy.ppm.finance.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName (TSdInvoiceQuery)SearchDTO
 * @Description TODO
 * @createTime 2024年11月07日 10:12:00
 */
@Data
public class TSdInvoiceQuerySearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 224122198774133096L;

    /***/
    private BigDecimal id;
    /***/
    private BigDecimal businessType;
    /***/
    private String orderNum;
    /***/
    private String count;
    /***/
    private String orderChannel;
    /***/
    private String orderDate;
    /***/
    private String invoiceTypeCode;
    /***/
    private String typeCode;
    /***/
    private String specialInvoiceMark;
    /***/
    private String taxInclusivePriceMark;
    /***/
    private String taxMark;
    /***/
    private String requestType;
    /***/
    private String buyerMobileNum;
    /***/
    private String buyerEmail;
    /***/
    private String buyerWeChat;
    /***/
    private String buyerMemberId;
    /***/
    private String taxExclusiveTotalAmount;
    /***/
    private String taxTotalAmount;
    /***/
    private String taxInclusiveTotalAmount;
    /***/
    private String note;
    /***/
    private String deduction;
    /***/
    private String payee;
    /***/
    private String checker;
    /***/
    private String invoiceClerk;
    /***/
    private String infoFormNum;
    /***/
    private String applicationFormCode;
    /***/
    private String originalInvoiceCode;
    /***/
    private String originalInvoiceNo;
    /***/
    private String originalIssueDate;
    /***/
    private String buyerNature;
    /***/
    private String buyerTaxId;
    /***/
    private String buyerName;
    /***/
    private String buyerAddrTel;
    /***/
    private String buyerTel;
    /***/
    private String buyerFinancialAccount;
    /***/
    private String buyerAccount;
    /***/
    private String sellerTaxId;
    /***/
    private String sellerName;
    /***/
    private String sellerAddrTel;
    /***/
    private String sellerTel;
    /***/
    private String sellerFinancialAccount;
    /***/
    private String sellerAccount;
    /***/
    private String createByName;
    /***/
    private String updateByName;
}

