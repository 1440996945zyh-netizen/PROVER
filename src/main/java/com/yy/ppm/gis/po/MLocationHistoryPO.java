package com.yy.ppm.gis.po;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class MLocationHistoryPO implements Serializable {
    /**
     * 车辆id
     */
    private String macId;

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
     * 定位时间
     */
    private Date gpsTime;

    private static final long serialVersionUID = 1L;
}

