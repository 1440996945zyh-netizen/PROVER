/**
 * Copyright 2024 json.cn
 */
package com.yy.ppm.finance.bean.dto;

/**
 * Auto-generated: 2024-11-07 10:51:23
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/
 */
public class RedInvoiceApply {

    private String id;
    private String blueLetterInvoiceCode;
    private String blueLetterInvoiceNo;
    private String issueDate;
    private String buyerNature;
    private String buyerName;
    private String buyerTaxID;
    private String sellerName;
    private String sellerTaxID;
    private String taxInclusiveTotalAmount;
    private String taxExclusiveTotalAmount;
    private String taxTotalAmount;
    private String applyForReason;
    private String taxInclusivePriceMark;
    private String specialInvoiceMark;
    private String taxMark;
    private String reason;
    private String bluetaxExclusiveTotalAmount;
    private String bluetaxTotalAmount;

    public static class Group{
        private String id;
        private String item;
        private String specification;
        private String measurementDimension;
        private String quantity;
        private String price;
        private String amount;
        private String taxScheme;
        private String taxAmount;
        private String productCode;
        private String selfProductCode;
        private String zerotaxSchemeMark;
        private String blueLetterInvoiceID;
    }
}