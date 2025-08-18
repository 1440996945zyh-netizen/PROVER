package com.yy.ppm.finance.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 红字确认单(TSdRedInvoiceApply)SearchDTO
 * @Description TODO
 * @createTime 2024年11月07日 16:57:00
 */
@Data
public class TSdRedInvoiceApplySearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -53597571683162393L;

    /***/
    private BigDecimal id;
    /**
     * 发票id
     */
    private BigDecimal invoiceId;
    /**
     * 序号
     */
    private String num;
    /**
     * 红字确认单编号
     */
    private BigDecimal redLetterInfoFormNum;
    /**
     * 红字确认单uuid
     */
    private String applicationFormCode;
    /**
     * 信息表状态描述
     */
    private String statusDescription;
    /**
     * 确认单状态代码
     */
    private String statusCode;
    /***/
    private String createByName;
    /***/
    private String updateByName;
}

