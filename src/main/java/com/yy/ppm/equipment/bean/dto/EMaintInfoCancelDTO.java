package com.yy.ppm.equipment.bean.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 设备维修派工信息作废DTO
 * @author system
 */
@Data
public class EMaintInfoCancelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID列表
     */
    private List<Long> ids;

    /**
     * 作废备注
     */
    private String cancelRemark;
}

