package com.yy.ppm.equipment.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 物资入库明细表PO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialWarehouseInDetailPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 入库主表ID（外键关联E_MATERIAL_WAREHOUSE_IN表）
     */
    private Long warehouseInId;

    private Integer sortNum;

    /**
     * 物资申报明细ID（关联E_MATERIAL_APPLICATION_DETAIL表）
     */
    private Long applicationId;

    /**
     * 申报部门ID
     */
    private Long applicationDeptId;

    /**
     * 申报部门名称
     */
    private String applicationDeptName;

    /**
     * 采购数量
     */
    private BigDecimal purchaseQuantity;

    /**
     * 物资名称
     */
    private String materialName;

    /**
     * 规格型号
     */
    private String specification;

    /**
     * 规格描述
     */
    private String specificationDesc;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 供应商名称
     */
    private String supplierName;
    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 入库数量
     */
    private BigDecimal warehouseInQuantity;

    /**
     * 已入库数量汇总（所有入库单的入库数量汇总，用于计算未入库数量）
     */
    private BigDecimal warehouseInQuantitySum;

    /**
     * 已出库数量
     */
    private BigDecimal outQuantity;

    /**
     * 未出库数量
     */
    private BigDecimal remainingQuantity;

    /**
     * 计量单位
     */
    private String unit;

    /**
     * 计量单位编码
     */
    private String unitCode;

    /**
     * 不含税单价
     */
    private BigDecimal taxExcludedUnitPrice;

    /**
     * 含税单价
     */
    private BigDecimal taxIncludedUnitPrice;

    /**
     * 不含税金额
     */
    private BigDecimal taxExcludedAmount;

    /**
     * 含税金额
     */
    private BigDecimal taxIncludedAmount;

    /**
     * 已开票金额
     */
    private BigDecimal invoicedAmount;

    /**
     * 未开票金额
     */
    private BigDecimal uninvoicedAmount;

    /**
     * 税额
     */
    private BigDecimal taxAmount;

    /**
     * 税率
     */
    private BigDecimal taxRate;

    /**
     * 物资代码
     */
    private String materialCode;

    /**
     * 物资ID（关联E_MATERIAL_CODE表）
     */
    private Long materialId;

    /**
     * 质保到期时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date warrantyExpiryDate;

    /**
     * 采购员ID
     */
    private Long purchaserId;

    /**
     * 采购员名称
     */
    private String purchaserName;
}

