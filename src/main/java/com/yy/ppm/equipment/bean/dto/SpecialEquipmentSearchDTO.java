package com.yy.ppm.equipment.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;

/**
 * 特种设备查询DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class SpecialEquipmentSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备名称
     */
    private String equipName;

    /**
     * 设备编码
     */
    private String equipCode;

    /**
     * 注册登记代码
     */
    private String particularRegistrationCode;
}

