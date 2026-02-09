package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 物资申报明细PO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EMaterialApplicationDetailPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 物资申请表ID（外键关联E_MATERIAL_APPLICATION表）
     */
    private Long applicationId;

    /**
     * 物资代码ID（关联E_MATERIAL_CODE表）
     */
    private Long materialCodeId;

    /**
     * 物资代码
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
     * 计量单位
     */
    private String unit;

    /**
     * 申报数量
     */
    private BigDecimal applicationQuantity;

    /**
     * 估价
     */
    private BigDecimal estimatedPrice;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 流向类型：设备-01，其他-02
     */
    private String flowType;

    /**
     * 流向
     */
    private String flowDirection;

    /**
     * 设备ID列表（多个设备ID用逗号分隔）
     */
    private String equipIds;

    /**
     * 设备名称列表（多个设备名称用逗号分隔）
     */
    private String equipNames;

    /**
     * 供货时限
     */
    private String supplyTimeLimit;

    /**
     * 建议品牌
     */
    private String suggestedBrand;

    /**
     * 规格描述
     */
    private String specificationDesc;

    /**
     * 物资采购明细ID（关联E_MATERIAL_PURCHASE_DETAIL表）
     */
    private Long purchaseDetailId;

    /**
     * 申报时库存
     */
    private BigDecimal applicationStockQuantity;
}

