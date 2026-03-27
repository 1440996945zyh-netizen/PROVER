package com.yy.ppm.equipment.bean.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 二维码实体对象信息
 * @author system
 */
@Data
public class EquipQrCodeInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备id
     */
    private Long equipId;

    /**
     * 设备大类id
     */
    private Long equipBigCategoryId;

    /**
     * 设备中类id
     */
    private Long equipMiddleCategoryId;

    /**
     * 设备小类id
     */
    private Long equipSmallCategoryId;


    /**
     * 设备机构ID
     */
    private Long equipInstitutionId;

    /**
     * 设备机构名称
     */
    private String equipInstitutionName;

    /**
     * 设备部件ID
     */
    private Long equipUnitId;

    /**
     * 设备部件名称
     */
    private String equipUnitName;
}
