package com.yy.ppm.equipment.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;

/**
 * 物资出库查询DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialWarehouseOutSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 出库单号
     */
    private String warehouseOutNo;
    private String warehouseName;

    /**
     * 出库主题
     */
    private String warehouseOutTitle;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 所属仓库ID
     */
    private Long warehouseId;
}

