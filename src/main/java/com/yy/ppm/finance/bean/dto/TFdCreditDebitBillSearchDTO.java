package com.yy.ppm.finance.bean.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.page.PageParameter;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 贷方解放票据主表(TFdCreditDebitBill)SearchDTO
 * @Description TODO
 * @createTime 2023年10月08日 16:19:00
 */
@Data
public class TFdCreditDebitBillSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 273240205376776209L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 公司id
     */
    private Long companyId;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 税务发票类型Id
     */
    private Long taxationInvoiceCode;
    /**
     * 税务发票类型Name
     */
    private String taxationInvoice;
    /**
     * 贷方票据类型Code
     */
    private Long cndnBillTypeCode;
    /**
     * 贷方票据类型Name
     */
    private String cndnBillTypeName;
    /**
     * 发票号码
     */
    private String invoiceCode;
    /**
     * 付款人id
     */
    private Long customerId;
    /**
     * 付款人NAME
     */
    private String customerName;
    /**
     * 系统发票号码（VI/CI/MI）
     */
    private String sysInvoiceCode;
    /**
     * 冲销金额
     */
    private Long eliminateAmount;
    /**
     * 备注
     */
    private String remark;
    /**
     * CNDN类型
     */
    @NotBlank(message = "借贷方类型不允许为空！")
    private Long cndnType;
    /**
     * 是否作废
     */
    private Long status;
    /**
     * 作废原因
     */
    private String voidReason;
    /**
     * 作废备注
     */
    private String voidRemark;
    /**
     * 是否付款收据
     */
    private Long isDebtorPayment;
    /**
     * 创建者-姓名
     */
    private String createByName;
    /**
     * 更新者-姓名
     */
    private String updateByName;

    /**
     * cndnCode
     */
    private String cndnCode;
    /**
     * cndn 开具时间
     */
    private String cndnTime;

}

