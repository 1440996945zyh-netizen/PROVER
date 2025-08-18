package com.yy.ppm.finance.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 贷方解放票据主表(TFdCreditDebitBillDetail)PO
 * @Description
 * @createTime 2023年10月08日 16:19:00
 */
@Setter
@Getter
public class TFdCreditDebitBillDetailPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 203486777277601549L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * CNDN主表ID
     */
    private Long cndnId;
    /**
     * 发票子表ID，冲销子表id
     */
    private Long invoiceDetailId;
    /**
     * 港口代码
     */
    private Long portCode;
    /**
     * 港口名称
     */
    private String portName;
    /**
     * 数量
     */
    private BigDecimal mea;
    /**
     * 数量2
     */
    private BigDecimal numberCount2;
    /**
     * 单位
     */
    private String unitCode;
    /**
     * 单位
     */
    private String unitName;
    /**
     * 费率名称
     */
    private String rateCode;
    /**
     * 费率名称（项目概述
     */
    private String rateName;
    /**
     * 原始费率
     */
    private BigDecimal oldRate;
    /**
     * 新费率
     */
    private BigDecimal newRate;
    /**
     * 货物类型代码
     */
    private Long cargoTypeCode;
    /**
     * 货物代码
     */
    private String cargoCode;
    /**
     * 税率
     */
    private BigDecimal taxRate;
    /**
     * 税额
     */
    private BigDecimal taxAmount;
    /**
     * 付款方式
     */
    private Long paymentMethodCode;
    /**
     * 付款方式name
     */
    private String paymentMethodName;
    /**
     * 银行ID
     */
    private Long bankId;
    /**
     * 项目金额
     */
    private BigDecimal amount;

    /**
     * 备注
     */
    private String remark;
    /**
     * 新的创建时间
     */
    private  String createTimeNew;

    /**
     * 费率
     */
    private Long rateId;
}

