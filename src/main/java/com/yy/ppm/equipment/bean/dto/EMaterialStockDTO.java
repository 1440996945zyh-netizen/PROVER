package com.yy.ppm.equipment.bean.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * 物资库存DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialStockDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 仓库ID
     */
    @ExcelIgnore
    private Long warehouseId;

    /**
     * 仓库名称
     */
    @ExcelProperty(value = "仓库名称")
    @ColumnWidth(20)
    private String warehouseName;

    /**
     * 物资ID
     */
    @ExcelIgnore
    private Long materialId;

    /**
     * 物资名称
     */
    @ExcelProperty(value = "物资名称")
    @ColumnWidth(30)
    private String materialName;

    /**
     * 物资类别（三级拼起来）
     */
    @ExcelProperty(value = "物资类别")
    @ColumnWidth(30)
    private String categoryName;

    /**
     * 规格型号
     */
    @ExcelProperty(value = "规格型号")
    @ColumnWidth(20)
    private String specificationModel;

    /**
     * 品牌
     */
    @ExcelProperty(value = "品牌")
    @ColumnWidth(15)
    private String brand;

    /**
     * 计量单位编码
     */
    @ExcelIgnore
    private String unitCode;

    /**
     * 计量单位名称
     */
    @ExcelProperty(value = "计量单位")
    @ColumnWidth(12)
    private String unitName;

    /**
     * 库存数量（汇总）
     */
    @ExcelProperty(value = "库存数量")
    @ColumnWidth(15)
    private BigDecimal stockQuantity;

    /**
     * 库存单价（库存总价/库存数量）
     */
    @ExcelProperty(value = "单价(元)")
    @ColumnWidth(15)
    private BigDecimal stockUnitPrice;

    /**
     * 库存总价（汇总）
     */
    @ExcelProperty(value = "库存总价(元)")
    @ColumnWidth(18)
    private BigDecimal stockTotalPrice;

    @ExcelIgnore
    List<EMaterialStockDTO> detailList;
}

