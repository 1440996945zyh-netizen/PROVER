package com.yy.ppm.equipment.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;

/**
 * 物资入库查询DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialWarehouseInSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 入库单号
     */
    private String warehouseInNo;

    /**
     * 入库主题
     */
    private String warehouseInTitle;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 入库类型编码
     */
    private String warehouseInTypeCode;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 所属仓库ID
     */
    private Long warehouseId;

    /**
     * 验收状态：0-待验收，1-通过，2-不通过
     */
    private Integer acceptanceStatus;
}

