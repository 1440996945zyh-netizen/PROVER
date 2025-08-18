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
 * @ClassName 收据主表(TFdDebtorpayment)PO
 * @Description
 * @createTime 2023年09月20日 11:44:00
 */
@Data
public class TFdDebtorpaymentPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -23841609492846158L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 收据号（自动生成的)
     */
    private String debtorpaymentNo;
    /**
     * 收款方（作业公司)
     */
    private Long companyId;
    /**
     * 作业公司名称
     */
    private String companyName;
    /**
     * 付款人（客户)
     */
    private Long customerId;
    /**
     * 付款人名称（客户名称)
     */
    private String customerName;
    /**
     * 开具日期
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date debtorpaymentTime;
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 状态 (1正常 2作废
     */
    private Long status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 作废备注
     */
    private String voidRemark;

    /**
     * 预缴类型  10：货方  30:船方    40:杂项
     */

    private Long prepaymentTypeCode;
    /**
     * 预缴类型
     */
    private String prepaymentTypeName;

    /**
     * 收据类型
     */
    private String debTypeCode;
    private String debTypeName;

}

