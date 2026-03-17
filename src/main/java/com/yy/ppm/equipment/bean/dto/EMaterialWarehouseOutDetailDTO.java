package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.equipment.bean.po.EMaterialWarehouseOutDetailPO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 物资出库明细DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialWarehouseOutDetailDTO extends EMaterialWarehouseOutDetailPO {

    private static final long serialVersionUID = 1L;

    /**
     * 申领数量（从申请明细表获取）
     */
    private BigDecimal applicationQuantity;

    /**
     * 已申领数量（从申请明细表获取）
     */
    private BigDecimal outQuantitySum;

    /**
     * 未申领数量（计算字段：申领数量 - 已申领数量）
     */
    private BigDecimal remainingQuantity;
}

