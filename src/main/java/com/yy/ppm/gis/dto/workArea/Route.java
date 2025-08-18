package com.yy.ppm.gis.dto.workArea;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @Author linqi
 * @Description
 * @Date 2023-06-16 16:51
 */
@Setter
@Getter
public class Route {

    /**
     * 主键
     */
    private Long routeId;

    /**
     * 路线名称
     */
    private String routeName;

    /**
     * 路线起点ID
     */
    private Long beginPid;

    /**
     * 路线终点ID
     */
    private Long endPid;

    /**
     * 状态（1在用 0停用）
     */
    private Integer status;

    /**
     * 路线类型（0双行 1单行）
     */
    private Integer routeType;

    /**
     * 起点经度
     */
    private BigDecimal beginLon;

    /**
     * 起点纬度
     */
    private BigDecimal beginLat;

    /**
     * 终点经度
     */
    private BigDecimal endLon;

    /**
     * 终点纬度
     */
    private BigDecimal endLat;
}
