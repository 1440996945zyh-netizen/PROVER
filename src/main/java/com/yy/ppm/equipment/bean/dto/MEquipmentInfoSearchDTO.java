package com.yy.ppm.equipment.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;

/**
 * 设备台账信息查询DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class MEquipmentInfoSearchDTO extends PageParameter implements Serializable {

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
     * 设备大类ID
     */
    private Long equipBigCategoryId;

    /**
     * 设备中类ID
     */
    private Long equipMiddleCategoryId;

    /**
     * 设备小类ID
     */
    private Long equipSmallCategoryId;

    /**
     * 设备状态
     */
    private Long equipState;

    /**
     * 所属单位
     */
    private Long useCompanyId;

    /**
     * 所属部门
     */
    private Long useOrgId;
}

