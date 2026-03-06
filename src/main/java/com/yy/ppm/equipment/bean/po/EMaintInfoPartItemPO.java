package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 设备维保派工部位部件子表PO
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EMaintInfoPartItemPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 维保主表ID
     */
    private Long maintInfoId;

    /**
     * 设备小类ID
     */
    private Long equipSmallCategoryId;

    /**
     * 设备小类名称
     */
    private String equipSmallCategoryName;

    /**
     * 设备机构ID
     */
    private Long equipInstitutionId;

    /**
     * 设备机构名称
     */
    private String equipInstitutionName;

    /**
     * 设备部件ID
     */
    private Long equipUnitId;

    /**
     * 设备部件名称
     */
    private String equipUnitName;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 删除标志（0-未删除，1-已删除）
     */
    private Integer delFlag;

    /**
     * 删除人ID
     */
    private Long deleteBy;

    /**
     * 删除人名称
     */
    private String deleteByName;

    /**
     * 删除时间
     */
    private Date deleteTime;
}

