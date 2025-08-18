package com.yy.ppm.gis.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @Author linqi
 * @Description
 * @Date 2023-06-16 14:28
 */
@Setter
@Getter
public class TPlanWorkareaPO extends BasePO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 计划ID
     */
    @NotNull(message = "计划id不能为空")
    private Long planId;

    /**
     * 区域类型 1起点 2终点
     */
    @NotNull(message = "区域类型不能为空")
    private Integer areaType;

    /**
     * 区域名称
     */
    @NotBlank(message = "区域名称不能为空")
    private String areaName;

    /**
     * 经度1
     */
    @NotNull(message = "经度1不能为空")
    private BigDecimal lon1;

    /**
     * 维度1
     */
    @NotNull(message = "维度1不能为空")
    private BigDecimal lat1;

    /**
     * 经度2
     */
    @NotNull(message = "经度2不能为空")
    private BigDecimal lon2;

    /**
     * 维度2
     */
    @NotNull(message = "维度2不能为空")
    private BigDecimal lat2;

    /**
     * 经度3
     */
    @NotNull(message = "经度3不能为空")
    private BigDecimal lon3;

    /**
     * 维度3
     */
    @NotNull(message = "维度3不能为空")
    private BigDecimal lat3;

    /**
     * 经度4
     */
    private BigDecimal lon4;

    /**
     * 维度4
     */
    private BigDecimal lat4;

    /**
     * 经度5
     */
    private BigDecimal lon5;

    /**
     * 维度5
     */
    private BigDecimal lat5;

    /**
     * 经度6
     */
    private BigDecimal lon6;

    /**
     * 维度6
     */
    private BigDecimal lat6;
}
