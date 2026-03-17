package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 物资出库申请子表PO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialOutApplicationDetailPO extends BasePO {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 出库申请主表ID（外键关联E_MAT_WAREHOUSE_OUT_APP表）
     */
    private Long outApplicationId;

    /**
     * 物资ID（关联E_MATERIAL_CODE表）
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
     * 品牌
     */
    private String brand;

    /**
     * 申请数量
     */
    private BigDecimal applicationQuantity;

    /**
     * 流向
     */
    private String flowDirection;

    /**
     * 流向类型：设备-01，其他-02
     */
    private String flowType;

    /**
     * 设备ID列表（多个设备ID用逗号分隔）
     */
    private String equipIds;

    /**
     * 设备名称列表（多个设备名称用逗号分隔）
     */
    private String equipNames;

    /**
     * 已出库数量
     */
    private BigDecimal outQuantitySum;
}

