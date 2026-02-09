package com.yy.ppm.equipment.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;

/**
 * 物资采购查询DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialPurchaseSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 采购单号
     */
    private String purchaseNo;

    /**
     * 采购单主题
     */
    private String purchaseTitle;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 采购类型编码
     */
    private String purchaseTypeCode;

    /**
     * 采购状态：0-采购成功，1-采购失败
     */
    private Integer purchaseStatus;
}

