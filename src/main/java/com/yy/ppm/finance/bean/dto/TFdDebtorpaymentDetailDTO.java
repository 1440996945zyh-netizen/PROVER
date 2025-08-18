package com.yy.ppm.finance.bean.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.finance.bean.po.TFdDebtorpaymentDetailPO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 收据主表(TFdDebtorpaymenDetail)DTO
 * @Description
 * @createTime 2023年09月20日 11:44:00
 */
@Data
public class TFdDebtorpaymentDetailDTO extends TFdDebtorpaymentDetailPO {

    private static final long serialVersionUID = -71436156717336690L;

    //开局时间

    /***
     * 发票或者预缴的开具时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date invoicePrepayTime;
    /**
     * 客户名称
     */
    private String customerName;
    /**
     * 预缴的预缴金额
     */
    private BigDecimal utilizedAmount;

    /**
     * 预缴表状态
     */
    private Long status;
    /**
     * 预缴作废原因
     */
    private String voidRemark;
    /**
     * cndn类型
     */
    private Long cndnType;


    private Long bankId;
    private String bankName;
    //对账类型
    private String prepaymentTypeCode;

    //预缴方式
    private String prepayModeCode;
    //票货id
    private Long cargoInfoId;

    private String cargoInfoNo;

    /**
     * 未作付款收据的金额
     */
    private BigDecimal hasNotReceiptAmount;
    /**
     * 已做付款收据的金额
     */
    private BigDecimal hasReceiptAmount;
    /**
     * 付款收据状态  10 未作 30 已做
     */
    private String receiptStatus;
}
