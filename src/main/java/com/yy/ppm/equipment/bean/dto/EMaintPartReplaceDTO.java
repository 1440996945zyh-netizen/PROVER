package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.equipment.bean.po.EMaintPartReplacePO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 设备维修配件更换DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaintPartReplaceDTO extends EMaintPartReplacePO {

    private static final long serialVersionUID = 1L;

    /**
     * 未使用数量（计算字段：申领数量 - 已使用数量）
     */
    private BigDecimal unusedQuantity;
}

