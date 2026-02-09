package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 物资出库明细表PO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialWarehouseOutDetailPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 物资出库主表ID（外键关联E_MAT_WAREHOUSE_OUT表）
     */
    private Long warehouseOutId;

    /**
     * 物资信息ID（关联E_MATERIAL_CODE表）
     */
    private Long materialId;

    /**
     * 物资code
     */
    private String materialCode;

    /**
     * 物资名称
     */
    private String materialName;

    /**
     * 规格型号
     */
    private String specificationModel;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 计量单位编码
     */
    private String unitCode;

    /**
     * 计量单位名称
     */
    private String unitName;

    /**
     * 物资出库申请明细ID（关联E_MAT_WAREHOUSE_OUT_APP_DETAIL表）
     */
    private Long warehouseOutAppDetailId;

    /**
     * 出库数量
     */
    private BigDecimal outQuantity;

    /**
     * 流向
     */
    private String flowDirection;
}

