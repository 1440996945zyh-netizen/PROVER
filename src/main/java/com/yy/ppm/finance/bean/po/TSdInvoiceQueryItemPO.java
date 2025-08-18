package com.yy.ppm.finance.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName (TSdInvoiceQueryItem)PO
 * @Description
 * @createTime 2024年11月07日 10:13:00
 */
@Data
public class TSdInvoiceQueryItemPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 106186810828698383L;

    /**
     *
     */
    private Long id;
    /**
     *
     */
    private Long invoiceQueryId;
    /**
     *
     */
    private String natureLines;
    /**
     *
     */
    private String item;
    /**
     *
     */
    private BigDecimal shorterItem;
    /**
     *
     */
    private String specification;
    /**
     *
     */
    private String measurementDimension;
    /**
     *
     */
    private String quantity;
    /**
     *
     */
    private String price;
    /**
     *
     */
    private String amount;
    /**
     *
     */
    private String taxScheme;
    /**
     *
     */
    private String taxAmount;
    /**
     *
     */
    private String productCode;
    /**
     *
     */
    private String selfProductCode;
    /**
     *
     */
    private String zerotaxSchemeMark;
    /**
     *
     */
    private String blueLetterInvoiceId;

}

