package com.yy.ppm.finance.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName (TSdInvoiceQueryItem)SearchDTO
 * @Description TODO
 * @createTime 2024年11月07日 10:13:00
 */
@Data
public class TSdInvoiceQueryItemSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 986476240925480944L;

    /***/
    private BigDecimal id;
    /***/
    private Long invoiceQueryId;
    /***/
    private String natureLines;
    /***/
    private String item;
    /***/
    private BigDecimal shorterItem;
    /***/
    private String specification;
    /***/
    private String measurementDimension;
    /***/
    private String quantity;
    /***/
    private String price;
    /***/
    private String amount;
    /***/
    private String taxScheme;
    /***/
    private String taxAmount;
    /***/
    private String productCode;
    /***/
    private String selfProductCode;
    /***/
    private String zerotaxSchemeMark;
    /***/
    private String blueLetterInvoiceId;
    /***/
    private String createByName;
    /***/
    private String updateByName;
}

