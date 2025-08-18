package com.yy.ppm.finance.bean.dto;


import com.yy.ppm.finance.bean.po.TFdCreditDebitBillDetailPO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.hpsf.Decimal;

import java.math.BigDecimal;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 贷方解放票据主表(TFdCreditDebitBillDetail)DTO
 * @Description
 * @createTime 2023年10月08日 16:19:00
 */
@Setter
@Getter
public class TFdCreditDebitBillDetailDTO extends TFdCreditDebitBillDetailPO {

    private static final long serialVerionUID = 910995835073665500L;

    /**
     * 系统发票编号
     */
    private String sysInvoiceCode;
    /**
     * 发票编号
     */
    private String invoiceCode;
    /**
     * 付款人
     */
    private String customerId;
    private String customerName;
    /**
     *  公司名称
     */
    private String companyId;
    private String companyName;
    /**
     * 序号
     */
    private Integer serialNumber;
    /**
     * 发票总金额 （冲销总金额
     */
    private BigDecimal eliminateAmount;
    /**
     * 发票类型
     */
    private Long invoiceTypeCode;
    /**
     * 单价
     */
    private BigDecimal pieceAmount;
    private Long rateId;
    private String cargoName;

    private String cndnType;
    private String cndnBillTypeCode;
    private String itemTypeCd;
    private String productCode;
}
