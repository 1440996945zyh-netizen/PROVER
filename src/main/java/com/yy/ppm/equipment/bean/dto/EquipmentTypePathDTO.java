package com.yy.ppm.equipment.bean.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 设备类型完整路径DTO（用于返回大类、中类、小类信息）
 * @author system
 * @version 1.0.0
 */
@Data
public class EquipmentTypePathDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备大类ID
     */
    private Long equipBigCategoryId;

    /**
     * 设备大类名称
     */
    private String equipBigCategoryName;

    /**
     * 设备中类ID
     */
    private Long equipMiddleCategoryId;

    /**
     * 设备中类名称
     */
    private String equipMiddleCategoryName;

    /**
     * 设备小类ID
     */
    private Long equipSmallCategoryId;

    /**
     * 设备小类名称
     */
    private String equipSmallCategoryName;
}

