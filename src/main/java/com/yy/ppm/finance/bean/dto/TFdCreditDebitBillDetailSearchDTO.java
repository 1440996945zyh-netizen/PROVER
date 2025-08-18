package com.yy.ppm.finance.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 贷方解放票据主表(TFdCreditDebitBillDetail)SearchDTO
 * @Description TODO
 * @createTime 2023年10月08日 16:19:00
 */
@Data
public class TFdCreditDebitBillDetailSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 285667364999294308L;

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
    private Long mea;
    /**
     * 发票项目号码
     */
    private String billUnit;
    /**
     * 费率
     */
    private String rateCode;
    /**
     * 费率名称（项目概述
     */
    private Long rateName;
    /**
     * 原始费率
     */
    private Long oldRate;
    /**
     * 新费率
     */
    private Long newRate;
    /**
     * 货物类型代码
     */
    private Long cargoTypeCode;
    /**
     * 货物代码
     */
    private Long cargoCode;
    /**
     * 税率
     */
    private Long taxRate;
    /**
     * 税额
     */
    private Long taxAmount;
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
    private Long amount;
    /**
     * 创建者-姓名
     */
    private String createByName;
    /**
     * 更新者-姓名
     */
    private String updateByName;
}

