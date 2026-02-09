package com.yy.ppm.equipment.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;

/**
 * 物资库存盘点明细查询DTO（包含入库信息）
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialStockCheckDetailSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 盘点单ID（必填）
     */
    private Long checkId;

    /**
     * 物资名称（模糊查询）
     */
    private String materialName;

    /**
     * 规格型号（模糊查询）
     */
    private String specificationModel;

    /**
     * 入库单号（模糊查询）
     */
    private String warehouseInNo;

    /**
     * 入库主题（模糊查询）
     */
    private String warehouseInTitle;

    /**
     * 供应商名称（模糊查询）
     */
    private String supplierName;

    /**
     * 差异类型：0-无差异，1-盘盈，2-盘亏
     */
    private Integer differenceType;

    /**
     * 盘点状态：0-待盘点，1-已盘点
     */
    private Integer checkStatus;
}

