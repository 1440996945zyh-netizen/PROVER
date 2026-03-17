package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.equipment.bean.po.EMaterialOutApplicationDetailPO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 物资出库申请明细DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialOutApplicationDetailDTO extends EMaterialOutApplicationDetailPO {

    private static final long serialVersionUID = 1L;

    /**
     * 库存数量（用于出库时显示）
     */
    private BigDecimal stockQuantity;

    // 注意：flowType、equipIds、equipNames 字段已从父类 EMaterialOutApplicationDetailPO 继承
}

