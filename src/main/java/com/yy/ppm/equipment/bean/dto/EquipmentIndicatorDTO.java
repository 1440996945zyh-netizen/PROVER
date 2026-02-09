package com.yy.ppm.equipment.bean.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 设备指标统计DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EquipmentIndicatorDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 月份（1-12）
     */
    private Integer month;

    /**
     * 自然时长（该月的小时数）
     */
    private BigDecimal naturalDuration;

    /**
     * 故障时长（设备维修表的维修时长，小时）
     */
    private BigDecimal faultDuration;

    /**
     * 运行台时（E_M_EQPT_OPERATION的运行台时，小时）
     */
    private BigDecimal runTime;

    /**
     * 故障率（故障时长/自然时长）
     */
    private BigDecimal failureRate;

    /**
     * 利用率（运行台时/自然时长）
     */
    private BigDecimal utilizationRate;

    /**
     * 完好率（(自然时长 - 维修时长) / 自然时长）
     */
    private BigDecimal goodRate;
}

