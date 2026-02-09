package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 物资入库出库关系表PO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialWarehouseInOutRelPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 物资入库明细ID（关联E_MATERIAL_WAREHOUSE_IN_DETAIL表）
     */
    private Long warehouseInDetailId;

    /**
     * 物资出库明细ID（关联E_MAT_WAREHOUSE_OUT_DETAIL表）
     */
    private Long warehouseOutDetailId;

    /**
     * 数量
     */
    private BigDecimal quantity;
}

