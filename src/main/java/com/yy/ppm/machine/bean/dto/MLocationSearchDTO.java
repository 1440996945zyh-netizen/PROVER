package com.yy.ppm.machine.bean.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.page.PageParameter;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 实时车辆表(MLocation)SearchDTO
 * @Description
 * @createTime 2023年10月25日 10:21:00
 */
@Data
public class MLocationSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 197391320772679844L;

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
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gpsTime;
    /**
     * 0无效解 1单点定位解(差分) 2伪距差分解 4固定解 5浮动解
     */
    private String gpsStatus;
}

