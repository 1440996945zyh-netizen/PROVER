package com.yy.ppm.equipment.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 物资库存明细DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialStockDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 物资名称
     */
    private String materialName;

    /**
     * 供应商
     */
    private String supplierName;

    /**
     * 规格描述
     */
    private String specificationDesc;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 采购数量
     */
    private BigDecimal purchaseQuantity;

    /**
     * 入库数量
     */
    private BigDecimal warehouseInQuantity;

    /**
     * 已出库数量
     */
    private BigDecimal outQuantity;

    /**
     * 库存数量
     */
    private BigDecimal remainingQuantity;

    /**
     * 计量单位
     */
    private String unit;

    /**
     * 质保到期时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date warrantyExpiryDate;

    /**
     * 采购员
     */
    private String purchaserName;

    /**
     * 入库时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date warehouseInTime;

    /**
     * 入库单号
     */
    private String warehouseInNo;

    /**
     * 入库类型名称
     */
    private String warehouseInTypeName;
}

