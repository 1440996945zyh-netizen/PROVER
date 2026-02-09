package com.yy.ppm.equipment.bean.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 设备维修配件更换查询DTO（用于根据设备ID查询可用的出库单和申领单明细）
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaintPartReplaceQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 出库单号
     */
    private String warehouseOutNo;

    /**
     * 物资ID
     */
    private Long materialId;

    /**
     * 物资名称
     */
    private String materialName;

    /**
     * 规格型号
     */
    private String specificationModel;

    /**
     * 计量单位编码
     */
    private String unitCode;

    /**
     * 计量单位名称
     */
    private String unitName;

    /**
     * 申领数量
     */
    private java.math.BigDecimal applicationQuantity;

    /**
     * 出库单明细ID
     */
    private Long warehouseOutDetailId;

    /**
     * 已使用数量（计算字段）
     */
    private java.math.BigDecimal usedQuantity;

    /**
     * 未使用数量（计算字段：申领数量 - 已使用数量）
     */
    private java.math.BigDecimal unusedQuantity;
}

