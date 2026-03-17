package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 物资采购比价信息PO
 * @author system
 */
@Data
public class EMaterialPurchaseComparisonPO extends BasePO implements Serializable {

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
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 报价
     */
    private BigDecimal quotationPrice;
}

