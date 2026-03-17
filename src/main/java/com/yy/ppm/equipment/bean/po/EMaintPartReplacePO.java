package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 设备维修配件更换PO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EMaintPartReplacePO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 维修信息ID（关联E_MAINT_INFO表）
     */
    private Long maintInfoId;

    /**
     * 设备ID
     */
    private Long equipId;

    /**
     * 出库单号
     */
    private String warehouseOutNo;

    /**
     * 出库单明细ID（关联E_MAT_WAREHOUSE_OUT_DETAIL表）
     */
    private Long warehouseOutDetailId;

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
    private BigDecimal applicationQuantity;

    /**
     * 本次使用数量
     */
    private BigDecimal usedQuantity;
}

