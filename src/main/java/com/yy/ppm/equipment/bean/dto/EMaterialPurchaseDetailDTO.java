package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.equipment.bean.po.EMaterialPurchaseDetailPO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 物资采购明细DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialPurchaseDetailDTO extends EMaterialPurchaseDetailPO {

    private static final long serialVersionUID = 1L;

    /**
     * 申请单号（关联物资申报表）
     */
    private String applicationNo;

    /**
     * 申报主题（关联物资申报表）
     */
    private String applicationTitle;

    /**
     * 物资名称（关联物资申报明细表）
     */
    private String materialName;

    /**
     * 物资代码（关联物资申报明细表）
     */
    private String materialCode;

    /**
     * 物资类别名称（关联物资代码表和物资类别表）
     */
    private String categoryName;

    /**
     * 规格型号（关联物资申报明细表）
     */
    private String specificationModel;

    /**
     * 计量单位（关联物资申报明细表）
     */
    private String unit;

    /**
     * 申报数量（关联物资申报明细表）
     */
    private BigDecimal applicationQuantity;
}

