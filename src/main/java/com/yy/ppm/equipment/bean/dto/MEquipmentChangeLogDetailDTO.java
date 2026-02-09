package com.yy.ppm.equipment.bean.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 设备变更记录详情DTO
 * @author system
 */
@Data
public class MEquipmentChangeLogDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 变更记录主表ID
     */
    private Long changeLogId;

    /**
     * 变更字段
     */
    private String changeField;

    /**
     * 变更字段名称
     */
    private String changeFieldName;

    /**
     * 变更前值
     */
    private String oldValue;

    /**
     * 变更后值
     */
    private String newValue;
}

