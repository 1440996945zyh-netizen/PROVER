package com.yy.ppm.equipment.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;

/**
 * 设备变更记录查询DTO
 * @author system
 */
@Data
public class MEquipmentChangeLogSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    private Long equipId;

    /**
     * 变更类型：BASIC_INFO、FINANCE_SUPPLY、SPECIAL_INFO
     */
    private String changeType;
}

