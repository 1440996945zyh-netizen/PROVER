package com.yy.ppm.finance.bean.dto;


import com.yy.ppm.finance.bean.po.TFdDebtorpaymentPO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 收据主表(TFdDebtorpayment)DTO
 * @Description
 * @createTime 2023年09月20日 11:44:00
 */
@Data
public class TFdDebtorpaymentDTO extends TFdDebtorpaymentPO {

    private static final long serialVersionUID = 187908048105908099L;
    List<TFdDebtorpaymentDetailDTO> receiptList;
    //接收子类
    TFdDebtorpaymentDetailDTO formDataDo;

    /**
     * 重新计算金额 进行验证
     */
    private BigDecimal tmpAmount;

    /**
     * 用来接收前端的退还金额
     */
    private BigDecimal repayAmount;

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
