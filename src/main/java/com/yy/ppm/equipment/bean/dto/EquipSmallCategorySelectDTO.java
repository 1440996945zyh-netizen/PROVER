package com.yy.ppm.equipment.bean.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 设备小类下拉数据（来源：设备类别管理）
 * @author system
 */
@Data
public class EquipSmallCategorySelectDTO implements Serializable {

    private Long id;

    /** 设备小类名称 */
    private String name;

    /** 下拉显示文本（兼容前端 label/value 结构） */
    private String label;

    /** 下拉值（兼容前端 label/value 结构） */
    private Long value;
}
