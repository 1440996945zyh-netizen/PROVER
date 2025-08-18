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
 * @ClassName 发票子表(TFdInvoiceDetail)PO
 * @Description
 * @createTime 2023年09月15日 20:22:00
 */
@Data
public class TFdInvoiceDetailPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -19586532253843383L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 发票表ID
     */
    private Long invoiceId;
    /**
     * 结算单编号
     */
    private Long statementNo;
    /**
     * 费用类型  计算单表子表中的type字段 (10.货方结算单、20.陆集陆疏待定 ）30.船方计费 40.杂项计费 50：堆存费
     */
    private Long type;
    /**
     * 费目编号
     */
    private String rateItemCode;
    /**
     * 费目
     */
    private String rateItemName;
    /**
     * 数量1
     */
    private BigDecimal numberCount;
    /**
     * 数量2
     */
    private BigDecimal numberCount2;
    /**
     * 单价
     */
    private BigDecimal pieceAmount;
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 税率
     */
    private BigDecimal tax;
    /**
     * 税额
     */
    private BigDecimal taxAmount;

    /***
     * 结算单子表id
     */
    private Long statementDetailId;

    /**
     * 单位 字典 UNIT
     */

    private String unitCode;
    private String unitName;
    /**
     * 费率ID
     */
    private Long rateId;


    /**
     * 票货ID
     */
    private Long cargoInfoId;
    /**
     * 航次ID
     */
    private Long shipvoyageId;
    /**
     * 航次子表ID
     */
    private Long shipvoyageItemId;
    /**
     * SCN
     */
    private String scn;
    /**
     * 船名
     */
    private String shipName;
    /**
     * 航次
     */
    private String voyage;

    /**
     * 贸别 内贸 外贸
     */
    private String tradeType;
    /**
     * 进出口 进口 出口
     */
    private String impExp;
    /**
     * 开票方式
     */
    private String taxInvoiceCode;
    /**
     * 开票方式名称
     */
    private String taxInvoiceName;
    /**
     * 回执确认备注
     */
    private String receiptRemark;

}

