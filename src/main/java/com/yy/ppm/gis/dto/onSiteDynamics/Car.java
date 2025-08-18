package com.yy.ppm.gis.dto.onSiteDynamics;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author linqi
 * @Description
 * @Date 2023-06-06 17:23
 */
@Setter
@Getter
public class Car implements Serializable {

    /**
     * 车辆id
     */
    private String macId;

    /**
     * 车辆id
     */
    private String macName;

    /**
     * 机械类型编码
     */
    private String macTypeCode;

    /**
     * 机械类型名称
     */
    private String macTypeName;

    /**
     * 经度
     */
    private Double lon;

    /**
     * 纬度
     */
    private Double lat;

    /**
     * 速度
     */
    private Integer speed;

    /**
     * 方向
     */
    private String direction;

    /**
     * 定位时间
     */
    private String gpsTime;
    
    /**
     * 是否在线1：在线 0：下线
     */
    private String online = "0";
}
