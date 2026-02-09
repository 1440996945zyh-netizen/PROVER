package com.yy.ppm.equipment.bean.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 设备检修历史统计查询DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EquipmentMaintenanceSearchDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    private Long equipId;

    /**
     * 年份（如：2025）
     */
    private String year;
}

