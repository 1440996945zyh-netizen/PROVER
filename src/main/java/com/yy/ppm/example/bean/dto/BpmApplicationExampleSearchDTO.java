package com.yy.ppm.example.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.page.PageParameter;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class BpmApplicationExampleSearchDTO extends PageParameter implements Serializable {
    /**
     * 审批记录ID
     */
    private Long id;

    /**
     * 付款事由
     */
    private String paymentTitle;

    /**
     * 付款金额
     */
    private BigDecimal paymentAmount;

    /**
     * 收款方名称
     */
    private String payeeName;

    /**
     * 申请人ID
     */
    private Long applicantId;

    /**
     * 申请人姓名
     */
    private String applicantName;

    /**
     * 审批状态
     */
    private String approvalStatus;

    /**
     * 申请时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date applyTime;

    /**
     * 期望付款时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date expectedPaymentTime;
}
