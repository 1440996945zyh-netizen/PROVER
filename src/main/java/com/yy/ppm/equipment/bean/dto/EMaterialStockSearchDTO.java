package com.yy.ppm.equipment.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

/**
 * 物资库存查询DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialStockSearchDTO extends PageParameter {

    private static final long serialVersionUID = 1L;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 仓库名称
     */
    private String warehouseName;

    /**
     * 物资ID
     */
    private Long materialId;

    /**
     * 物资名称
     */
    private String materialName;

    /**
     * 物资类别ID
     */
    private Long categoryId;

    /**
     * 规格型号
     */
    private String specificationModel;

    /**
     * 品牌
     */
    private String brand;
}

