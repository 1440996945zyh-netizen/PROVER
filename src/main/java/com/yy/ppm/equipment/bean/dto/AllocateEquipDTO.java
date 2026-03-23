package com.yy.ppm.equipment.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 调拨设备选择DTO
 * @author system
 */
@Getter
@Setter
@ToString
public class AllocateEquipDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    private Long equipId;

    /**
     * 设备编码
     */
    private String equipCode;

    /**
     * 设备名称
     */
    private String equipName;

    /**
     * 设备规格
     */
    private String equipSpecification;

    /**
     * 型号
     */
    private String modelNumber;

    /**
     * 设备大类名称
     */
    private String equipBigCategoryName;

    /**
     * 设备中类名称
     */
    private String equipMiddleCategoryName;

    /**
     * 设备小类名称
     */
    private String equipSmallCategoryName;

    /**
     * 调出单位ID
     */
    private Long useCompanyId;
    /**
     * 调出单位名称
     */
    private String useCompanyName;

    /**
     * 调出部门ID
     */
    private Long useOrgId;

    /**
     * 调出部门名称
     */
    private String useOrgName;


    /**
     * 资产编码
     */
    private String equipAssetsCode;

    /**
     * 折旧年限
     */
    private Integer depreciationLimit;

    /**
     * 已折旧年限
     */
    private Integer alreadyLimit;

    /**
     * 净值
     */
    private BigDecimal netValue;

    /**
     * 原值
     */
    private BigDecimal price;

    /**
     * 购买日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date equipBuyDate;

    /**
     * 设备状态
     */
    private String equipState;

    /**
     * 设备状态名称
     */
    private String equipStateName;

    /**
     * 备注
     */
    private String remark;
    /**
     * 设备负责人
     */
    private Long responsiCode;
    /**
     * 设备负责人
     */
    private String responsiName;


    /**
     * 变更历史记录JSON
     */
    private String lastChangeInfo;


}
