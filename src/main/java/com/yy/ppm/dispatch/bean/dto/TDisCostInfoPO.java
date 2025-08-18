package com.yy.ppm.dispatch.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TDisCostInfoPO {
    /**
     * 作业公司
     */
    private String companyName;
    /**
     * 付款人name(客户名称name
     */
    private String customerName;

    /**
     * 结算单编号
     */
    private Long statementNo;
    /**
     * 费用类型  计算单表子表中的type字段 (10.货方结算单、20.陆集陆疏待定 ）30.船方计费 40.杂项计费 50：堆存费
     */
    private Long type;
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
    /**
     * SCN
     */
    private String scn;
    /**
     * 开票日期
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date invoiceTime;
    /**
     * 系统发票编号
     */
    private String sysInvoiceCode;
    /**
     * 船名航次
     */
    private String shipNameVoyage;


}
