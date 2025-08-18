package com.yy.ppm.machine.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 实时车辆表(MLocation)PO
 * @Description
 * @createTime 2023年10月25日 10:21:00
 */
@Data
public class MLocationPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 136153700042081003L;

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

