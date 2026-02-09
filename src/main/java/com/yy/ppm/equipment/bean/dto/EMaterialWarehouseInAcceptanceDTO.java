package com.yy.ppm.equipment.bean.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 物资入库验收DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialWarehouseInAcceptanceDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 入库单ID
     */
    private Long id;

    /**
     * 验收状态：0-待验收，1-通过，2-不通过
     */
    private Integer acceptanceStatus;

    /**
     * 验收备注
     */
    private String acceptanceRemarks;
}
