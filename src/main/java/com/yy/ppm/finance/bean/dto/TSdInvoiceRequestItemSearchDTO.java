package com.yy.ppm.finance.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName (TSdInvoiceRequestItem)SearchDTO
 * @Description TODO
 * @createTime 2024年11月07日 10:15:00
 */
@Data
public class TSdInvoiceRequestItemSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -96064660681534751L;

    /***/
    private BigDecimal id;
    /***/
    private BigDecimal invoiceRequestId;
    /***/
    private String serialNumber;
    /***/
    private String natureLines;
    /***/
    private String item;
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
    private String taxMark;
    /***/
    private String productCode;
    /***/
    private String selfProductCode;
    /***/
    private String zeroTaxSchemeMark;
    /***/
    private String createByName;
    /***/
    private String updateByName;
}

