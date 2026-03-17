package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 物资采购明细表PO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EMaterialPurchaseDetailPO extends BasePO {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 采购主表ID（外键关联E_MATERIAL_PURCHASE表）
     */
    private Long purchaseId;

    /**
     * 物资申报明细ID（关联E_MATERIAL_APPLICATION_DETAIL表）
     */
    private Long applicationDetailId;

    /**
     * 申报数量
     */
    private BigDecimal applicationQuantity;

    /**
     * 采购数量
     */
    private BigDecimal purchaseQuantity;

    /**
     * 税率
     */
    private BigDecimal taxRate;

    /**
     * 含税单价
     */
    private BigDecimal taxIncludedUnitPrice;

    /**
     * 不含税单价
     */
    private BigDecimal taxExcludedUnitPrice;

    /**
     * 含税金额
     */
    private BigDecimal taxIncludedAmount;

    /**
     * 不含税金额
     */
    private BigDecimal taxExcludedAmount;

    /**
     * 税额
     */
    private BigDecimal taxAmount;
}

