package com.yy.ppm.equipment.bean.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 设备备品备件DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EquipmentSpareDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 入库明细ID
     */
    private Long id;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 仓库名称
     */
    private String warehouseName;

    /**
     * 物资编码
     */
    private String materialCode;

    /**
     * 物资名称
     */
    private String materialName;

    /**
     * 物资类别名
     */
    private String categoryName;

    /**
     * 规格型号
     */
    private String specification;

    /**
     * 可使用库存（未出库数量）
     */
    private BigDecimal availableStock;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 计量单位
     */
    private String unit;
}

