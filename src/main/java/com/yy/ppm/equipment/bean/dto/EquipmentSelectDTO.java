package com.yy.ppm.equipment.bean.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 设备选择DTO（用于下拉框）
 * @author system
 */
@Data
public class EquipmentSelectDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    private Long id;

    /**
     * 设备编码
     */
    private String equipCode;

    /**
     * 设备名称
     */
    private String equipName;

    /**
     * 设备大类名称
     */
    private String equipBigCategoryName;

    /**
     * 设备中类名称
     */
    private String equipMiddleCategoryName;

    /**
     * 设备小类名称
     */
    private String equipSmallCategoryName;

    /**
     * 显示标签（大类 - 中类 - 小类 - 设备名称）
     */
    private String label;

    /**
     * 值（设备ID）
     */
    private Long value;
}

