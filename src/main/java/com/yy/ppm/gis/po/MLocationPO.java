package com.yy.ppm.gis.po;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class MLocationPO implements Serializable {
    /**
     * 主键
     */
    private Long id;

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

    /**
     * 车牌号、机械编号
     */
    private String macNm;

    /**
     * 0无效解 1单点定位解(差分) 2伪距差分解 4固定解 5浮动解
     */
    private String gpsStatus;

    private static final long serialVersionUID = 1L;
}

