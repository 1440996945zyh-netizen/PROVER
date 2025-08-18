package com.yy.ppm.gis.dto.onSiteDynamics;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @Author linqi
 * @Description
 * @Date 2023-06-14 14:25
 */
@Setter
@Getter
public class CarHistory {

    /**
     * 车辆id
     */
    private String macId;

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
    private Date gpsTimeD;

    /**
     * 定位时间
     */
    private String gpsTime;
}
