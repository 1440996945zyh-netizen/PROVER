package com.yy.ppm.equipment.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;

/**
 * 物资申报明细关联采购明细查询DTO（用于入库时选择）
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialApplicationDetailForWarehouseInSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 申请单号
     */
    private String applicationNo;

    /**
     * 采购单号
     */
    private String purchaseNo;

    /**
     * 物资名称
     */
    private String materialName;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 供应商ID
     */
    private Long supplierId;
}

