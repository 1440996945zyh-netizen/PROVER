package com.yy.ppm.tallyExtrinsic.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Auther wangxd
 * @Description 吨包前沿确认磅单
 * @Date 2024-06-13 16:33
 */
@Setter
@Getter
public class TBusReservationPoundPO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 计划编号
     */
    private String planId;

    /**
     * 开工时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 完工时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 状态：1 开始 2 结束
     */
    private String workStatus;


    /**
     * 车牌号
     */
    private String truckNo;
    /**
     * 二次派工数据id
     */
    private String secondaryId;
}
