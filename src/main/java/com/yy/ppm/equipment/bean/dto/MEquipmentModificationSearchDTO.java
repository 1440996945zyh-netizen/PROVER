package com.yy.ppm.equipment.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备改造记录查询DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MEquipmentModificationSearchDTO extends PageParameter {

    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    private Long equipId;

    /**
     * 改造类型编码
     */
    private String modifyTypeCode;
}

