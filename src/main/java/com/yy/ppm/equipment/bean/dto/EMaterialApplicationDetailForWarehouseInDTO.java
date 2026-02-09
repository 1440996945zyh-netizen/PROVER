package com.yy.ppm.equipment.bean.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 物资申报明细关联采购明细DTO（用于入库时选择）
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialApplicationDetailForWarehouseInDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 申报明细ID
     */
    private Long applicationDetailId;

    /**
     * 采购明细ID
     */
    private Long purchaseDetailId;

    /**
     * 采购主表ID
     */
    private Long purchaseId;

    /**
     * 物资编码
     */
    private String materialCode;

    /**
     * 物资ID（关联E_MATERIAL_CODE表）
     */
    private Long materialId;

    /**
     * 物资名称
     */
    private String materialName;

    /**
     * 供应商ID（从采购主表获取）
     */
    private Long supplierId;

    /**
     * 供应商名称（从采购主表获取）
     */
    private String supplierName;

    /**
     * 规格型号
     */
    private String specificationModel;

    /**
     * 建议品牌
     */
    private String suggestedBrand;

    /**
     * 品牌（从物资表获取）
     */
    private String brand;

    /**
     * 申报数量
     */
    private BigDecimal applicationQuantity;

    /**
     * 采购单价(元) - 含税单价
     */
    private BigDecimal taxIncludedUnitPrice;

    /**
     * 采购金额(元) - 含税金额
     */
    private BigDecimal taxIncludedAmount;

    /**
     * 税率(%)
     */
    private BigDecimal taxRate;

    /**
     * 计量单位
     */
    private String unit;

    /**
     * 供货时限
     */
    private String supplyTimeLimit;

    /**
     * 流向设备
     */
    private String flowDirection;

    /**
     * 规格描述
     */
    private String specificationDesc;

    /**
     * 采购数量
     */
    private BigDecimal purchaseQuantity;

    /**
     * 已入库数量（从入库明细表汇总）
     */
    private BigDecimal warehouseInQuantity;

    /**
     * 未入库数量（采购数量 - 已入库数量）
     */
    private BigDecimal remainingQuantity;

    /**
     * 申报单号
     */
    private String applicationNo;

    /**
     * 申报主题
     */
    private String applicationTitle;

    /**
     * 申报类型
     */
    private String applicationTypeName;

    /**
     * 申报部门ID
     */
    private Long applicationDeptId;

    /**
     * 申报部门名称
     */
    private String applicationDeptName;

    /**
     * 采购表创建人ID（用于默认设置采购员）
     */
    private Long purchaseCreateBy;

    /**
     * 采购表创建人名称（用于默认设置采购员）
     */
    private String purchaseCreateByName;

    /**
     * 采购单号
     */
    private String purchaseNo;

    /**
     * 采购类型名称
     */
    private String purchaseTypeName;

    /**
     * 定点服务类别名称
     */
    private String fixedServiceCategoryName;
}

