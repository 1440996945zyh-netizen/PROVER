package com.yy.ppm.equipment.bean.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 维修人员下拉选项DTO
 */
@Data
public class EMaintRepairUserOptionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 维修人员ID
     */
    private Long repairId;

    /**
     * 维修人员名称
     */
    private String repairName;
}
