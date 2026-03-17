package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;

/**
 * 物资代码PO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialCodePO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 类别ID（关联物资类别表）
     */
    private Long categoryId;

    /**
     * 物资代码
     */
    private String materialCode;

    /**
     * 物资名称
     */
    private String materialName;

    /**
     * 采购类型编码
     */
    private String purchaseTypeCode;

    /**
     * 采购类型名称
     */
    private String purchaseTypeName;

    /**
     * 规格型号
     */
    private String specificationModel;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 计量单位编码
     */
    private String unitCode;

    /**
     * 计量单位名称
     */
    private String unitName;

    /**
     * 助记码
     */
    private String mnemonicCode;

    /**
     * 状态（0-启用，1-停用）
     */
    private String status;

    /**
     * 删除标志（0-未删除，1-已删除）
     */
    private Integer delFlag;

    /**
     * 删除人
     */
    private String deleteBy;

    /**
     * 删除人姓名
     */
    private String deleteByName;

    /**
     * 删除时间
     */
    private java.util.Date deleteTime;
}

