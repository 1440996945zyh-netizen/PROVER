package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 设备财务信息PO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MEquipmentFinancePO extends BasePO {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 设备ID
     */
    private Long equipId;

    /**
     * 设备资产编号
     */
    private String equipAssetsCode;

    /**
     * 设备原值
     */
    private BigDecimal price;

    /**
     * 资产净值
     */
    private BigDecimal netValue;

    /**
     * 折旧期限
     */
    private Long depreciationLimit;

    /**
     * 已折旧期限
     */
    private Long alreadyLimit;
}

