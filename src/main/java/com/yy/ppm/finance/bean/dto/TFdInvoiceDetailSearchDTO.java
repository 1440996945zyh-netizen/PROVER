package com.yy.ppm.finance.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 发票子表(TFdInvoiceDetail)SearchDTO
 * @Description TODO
 * @createTime 2023年09月15日 20:22:00
 */
@Data
public class TFdInvoiceDetailSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -91342148005702158L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 发票表ID
     */
    private String invoiceId;
    /**
     * 作业公司id
     */
    private Long statementNo;
    /**
     * 发票类型
     */
    private Long invoiceType;
    /**
     * 费目编号
     */
    private String rateItemCode;
    /**
     * 费目
     */
    private String rateItemName;
    /**
     * 数量
     */
    private Long numberCount;
    /**
     * 金额
     */
    private Long amount;
    /**
     * 税率
     */
    private Long tax;
    /**
     * 税额
     */
    private Long taxAmount;
    /**
     * 创建者-姓名
     */
    private String createByName;
    /**
     * 更新者-姓名
     */
    private String updateByName;
}

