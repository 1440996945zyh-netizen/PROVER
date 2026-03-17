package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 物资库存盘点明细表PO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EMaterialStockCheckDetailPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 盘点主表ID
     */
    private Long checkId;

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
     * 账面数量
     */
    private BigDecimal bookQuantity;

    /**
     * 盘点数量
     */
    private BigDecimal checkQuantity;

    /**
     * 差异数量（盘点数量-账面数量）
     */
    private BigDecimal differenceQuantity;

    /**
     * 差异类型：1-盘盈，2-盘亏，0-无差异
     */
    private Integer differenceType;

    /**
     * 入库明细ID（关联E_MATERIAL_WAREHOUSE_IN_DETAIL表）
     */
    private Long warehouseInDetailId;

    /**
     * 盘点状态：0-待盘点，1-已盘点
     */
    private Integer checkStatus;

    /**
     * 备注
     */
    private String remark;

    /**
     * 审核人ID
     */
    private Long auditBy;

    /**
     * 审核人姓名
     */
    private String auditByName;

    /**
     * 审核时间
     */
    private java.util.Date auditTime;
}

