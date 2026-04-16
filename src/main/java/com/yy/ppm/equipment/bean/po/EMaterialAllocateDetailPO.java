package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 物资调拨明细 PO。
 * 对应调拨单中的具体物资行项目。
 *
 * @author system
 */
@Getter
@Setter
@ToString
public class EMaterialAllocateDetailPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID。
     */
    private Long id;

    /**
     * 调拨主表ID。
     */
    private Long allocateId;

    /**
     * 明细顺序号。
     */
    private Integer sortNum;

    /**
     * 物资ID。
     */
    private Long materialId;

    /**
     * 物资编码。
     */
    private String materialCode;

    /**
     * 物资名称。
     */
    private String materialName;

    /**
     * 规格型号。
     */
    private String specification;

    /**
     * 规格描述。
     */
    private String specDesc;

    /**
     * 品牌。
     */
    private String brand;

    /**
     * 单位编码。
     */
    private String unitCode;

    /**
     * 单位名称。
     */
    private String unitName;

    /**
     * 调拨数量。
     */
    private BigDecimal allocQty;

    /**
     * 可调数量快照。
     */
    private BigDecimal availableQty;

    /**
     * 含税单价。
     */
    private BigDecimal taxInPrice;

    /**
     * 含税金额。
     */
    private BigDecimal taxInAmt;

    /**
     * 流向说明。
     */
    private String flowDirection;

    /**
     * 备注。
     */
    private String remark;

    /**
     * 关联调出库明细ID。
     */
    private Long outDetailId;

    /**
     * 关联调入库明细ID。
     */
    private Long inDetailId;
}
