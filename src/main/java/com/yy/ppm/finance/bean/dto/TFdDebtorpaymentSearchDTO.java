package com.yy.ppm.finance.bean.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.page.PageParameter;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 收据主表(TFdDebtorpayment)SearchDTO
 * @Description TODO
 * @createTime 2023年09月20日 11:44:00
 */
@Data
public class TFdDebtorpaymentSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -73386744499976569L;

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
     * 付款人（客户) ID
     */
    private Long customerId;
    /**
     * 票货ID
     */
    private Long cargoInfoId;
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
    private Long amount;
    /**
     * 状态
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
     * 创建者-姓名
     */
    private String createByName;
    /**
     * 更新者-姓名
     */
    private String updateByName;

    /**
     * 发票编号 (预缴编号)
     */
    private String invoicePrepayNo;
    /**
     * 系统发票号码(补缴时值为 1 )
     */
    private String sysInvoicePrepayNo;

    /**
     * 预缴类型  10：货方  30:船方    40:杂项
     */

    private Long prepaymentTypeCode;
    /**
     * 预缴类型
     */
    private String prepaymentTypeName;

    /**
     * 类型集合  发票查询用
     */
    private List<Long> typeList;
}

