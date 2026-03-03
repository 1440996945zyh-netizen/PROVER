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
 * @Author: fanxianjin
 * @Desc: 报废设备选择DTO
 * @Date: 2026/2/28 14:31
 */
@Getter
@Setter
@ToString
public class ScrapEquipDTO implements Serializable {

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
     * 使用公司名称
     */
    private String useCompanyName;
    /**
     * 使用公司ID
     */
    private Long useCompanyId;


    /**
     * 使用部门名称
     */
    private String useOrgName;

    /**
     * 使用部门ID
     */
    private Long useOrgId;

    /**
     * 设备资产编码
     */
    private String equipAssetsCode;

    /**
     * 折旧年限
     */
    private BigDecimal depreciationLimit;

    /**
     * 已折旧年限
     */
    private BigDecimal alreadyLimit;

    /**
     * 净值
     */
    private BigDecimal netValue;

    /**
     * 原值
     */
    private BigDecimal price;

    /**
     * 创建时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 设备状态
     */
    private String equipState;

    /**
     * 设备状态
     */
    private String equipStateName;

    /**
     * 报废变更记录（JSON数据）
     */
    private String lastChangeInfo;

}
