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
public class Ship implements Serializable {

    /**
     * 船舶id
     */
    private String id;

    /**
     * 船名
     */
    private String vesselName;

    /**
     * 首缆桩code
     */
    private String beginBollard;

    /**
     * 尾缆桩code
     */
    private String endBollard;

    /**
     * 舷型 10左舷/20右舷
     */
    private String gunwale;

    /**
     * 船宽
     */
    private Double width;

    /**
     * 首缆桩名字
     */
    private String beginBollardName;

    /**
     * 首缆桩经度
     */
    private Double beginBollardLon;

    /**
     * 首缆桩纬度
     */
    private Double beginBollardLat;

    /**
     * 尾缆桩名字
     */
    private String endBollardName;

    /**
     * 尾缆桩经度
     */
    private Double endBollardLon;

    /**
     * 尾缆桩维度
     */
    private Double endBollardLat;

    /**
     * 船头靠岸一侧经度
     */
    private Double lon1;

    /**
     * 船头靠岸一侧维度
     */
    private Double lat1;

    /**
     * 船尾靠岸一侧经度
     */
    private Double lon2;

    /**
     * 船尾靠岸一侧维度
     */
    private Double lat2;

    /**
     * 船尾靠海一侧经度
     */
    private Double lon3;

    /**
     * 船尾靠海一侧维度
     */
    private Double lat3;

    /**
     * 船头靠海一侧经度
     */
    private Double lon4;

    /**
     * 船头靠海一侧维度
     */
    private Double lat4;

    /**
     * 船头中心点经度
     */
    private Double lon5;

    /**
     * 船头中心点维度
     */
    private Double lat5;
}
