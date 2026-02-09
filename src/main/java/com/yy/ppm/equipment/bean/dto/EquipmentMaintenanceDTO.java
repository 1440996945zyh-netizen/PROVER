package com.yy.ppm.equipment.bean.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 设备检修历史统计DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EquipmentMaintenanceDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 月份（1-12）
     */
    private Integer month;

    /**
     * 点检次数（E_CHECK_TASK表status为2的点检数）
     */
    private Integer spotCheckCount;

    /**
     * 故障次数（设备维修表验收状态的数据）
     */
    private Integer faultCount;

    /**
     * 润滑保养次数（E_PM_MAINTAIN_PLAN表的保养计划数）
     */
    private Integer lubricationCount;
}

