package com.yy.ppm.gis.po;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Hu Jingjing
 * @version 1.0.0
 * @ClassName MAiaLocationPO.java
 * @Description TODO
 * @createTime 2023年09月21日 09:52:00
 */
@Data
public class MAiaLocationPO {
    /**
     * 船舶
     */
    private String name;
    /**
     * 船舶MMSI
     */
    private String mmsi;
    /**
     * 经度
     */
    private BigDecimal lon;

    /**
     * 维度
     */
    private BigDecimal lat;

    /**
     * 速度
     */
    private BigDecimal speed;

    /**
     * 方向
     */
    private String direction;

    /**
     * 方向
     */
    private BigDecimal heading;

    /**
     * 方向
     */
    private BigDecimal rot;
    /**
     * 方向
     */
    private String status;

    /**
     * 定位时间
     */
    private Date gpsTime;
}
