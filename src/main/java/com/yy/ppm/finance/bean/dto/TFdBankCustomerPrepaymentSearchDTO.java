package com.yy.ppm.finance.bean.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.page.PageParameter;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 客户预缴(TFdBankCustomerPrepayment)SearchDTO
 * @Description TODO
 * @createTime 2023年09月14日 10:30:00
 */
@Data
public class TFdBankCustomerPrepaymentSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 311627887195220427L;

    /**
     * 航次id
     */
    private Long voyageId;
    /**
     *票货id
     */
    private Long cargoInfoId;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 预缴编号
     */
    private String prepaymentCode;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 公司id
     */
    private Long companyId;
    /**
     * 预缴类型code
     */
    private String prepaymentTypeCode;
    /**
     * 预缴类型名称
     */
    private String prepaymentTypeName;
    /**
     * 客户ID
     */
    private String customerId;
    /**
     * 客户名称
     */
    private String customerName;
    /**
     * 预缴日期(默认不可更改)
     */
    private Date prepaymentTime;
    /**
     * 预缴金额
     */
    private Long prepaymentAmount;
    /**
     * 已用金额
     */
    private Long utilizedAmount;
    /**
     * 付款方式
     */
    private Long paymentMethodCode;
    /**
     * 付款方式名称
     */
    private String paymentMethodName;
    /**
     * 银行名称
     */
    private String bankName;
    /**
     * 银行ID
     */
    private Long bankId;
    /**
     * 状态  1正常 2作废
     */
    private Long status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建者-姓名
     */
    private String createByName;
    /**
     * 更新者-姓名
     */
    private String updateByName;



    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    private String trustNo;
    private String shipvoyageItemId;

    private Long busTrustId;
    /**
     * 开始时间(起)
     */
    private String searchStartTime;

    /**
     * 开始时间(止)
     */
    private String searchEndTime;

    private String businessNo;

    private String hiddenEsti;
}

