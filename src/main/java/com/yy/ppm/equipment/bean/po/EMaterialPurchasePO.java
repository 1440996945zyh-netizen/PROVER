package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 物资采购主表PO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EMaterialPurchasePO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 采购单主题
     */
    private String purchaseTitle;

    /**
     * 采购单号
     */
    private String purchaseNo;
    private String failureReason;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 采购类型编码
     */
    private String purchaseTypeCode;

    /**
     * 采购类型名称
     */
    private String purchaseTypeName;

    /**
     * 定点服务类别编码
     */
    private String fixedServiceCategoryCode;

    /**
     * 定点服务类别名称
     */
    private String fixedServiceCategoryName;

    /**
     * 含税金额
     */
    private BigDecimal taxIncludedAmount;

    /**
     * 不含税金额
     */
    private BigDecimal taxExcludedAmount;

    /**
     * 采购状态：0-待审核，1-审核通过，2-驳回
     */
    private Integer purchaseStatus;

    /**
     * 审核备注
     */
    private String approvalRemark;

    /**
     * 审核人ID
     */
    private Long approvalBy;

    /**
     * 审核人姓名
     */
    private String approvalByName;

    /**
     * 审核时间
     */
    @com.fasterxml.jackson.annotation.JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date approvalTime;


    private Long useCompanyId;
    private String useCompanyName;
}

