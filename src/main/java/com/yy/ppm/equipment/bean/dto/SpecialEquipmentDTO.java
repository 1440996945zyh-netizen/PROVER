package com.yy.ppm.equipment.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 特种设备查询结果DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class SpecialEquipmentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 使用单位名称
     */
    private String unitName;

    /**
     * 使用部门名称
     */
    private String useOrgName;

    /**
     * 设备小类名称
     */
    private String equipSmallCategoryName;

    /**
     * 设备名称
     */
    private String equipName;

    /**
     * 设备编号
     */
    private String equipCode;

    /**
     * 规格
     */
    private String specificCode;

    /**
     * 型号
     */
    private String modelNumber;

    /**
     * 注册登记代码
     */
    private String particularRegistrationCode;

    /**
     * 检验时间（发布时间）
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date releaseDate;

    /**
     * 到期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date expireDate;

    /**
     * 有效期(月)
     */
    private Long validDate;
}

