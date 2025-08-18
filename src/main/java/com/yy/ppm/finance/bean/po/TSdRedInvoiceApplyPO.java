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
 * @ClassName 红字确认单(TSdRedInvoiceApply)PO
 * @Description
 * @createTime 2024年11月07日 16:57:00
 */
@Data
public class TSdRedInvoiceApplyPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -21949385909977989L;

    /**
     *
     */
    private Long id;
    /**
     * 发票id
     */
    private Long invoiceId;
    /**
     * 序号
     */
    private String num;
    /**
     * 红字确认单编号
     */
    private String redLetterInfoFormNum;
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

}

