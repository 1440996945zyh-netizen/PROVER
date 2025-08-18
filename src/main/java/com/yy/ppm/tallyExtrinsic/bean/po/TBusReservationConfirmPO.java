package com.yy.ppm.tallyExtrinsic.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Auther wangxd
 * @Description 吨包前沿确认
 * @Date 2024-06-13 16:33
 */
@Setter
@Getter
public class TBusReservationConfirmPO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 磅单id
     */
    private Long poundId;

    private Long reservationPoundId;

    /**
     * 磅单编号
     */
    private String poundNo;

    /**
     * 确认到达时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date confirmStartTime;

    /**
     * 确认离开时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date confirmEndTime;

    /**
     * 计划编号
     */
    private String planNo;

    /**
     * 货名
     */
    private String goodsName;

    /**
     * 件数
     */
    private Integer quantity;

    /**
     * 状态：1 到达 2 离开
     */
    private String status;

    /**
     * 门机号
     */
    private String macNo;

    /**
     * 车牌号
     */
    private String truckNo;

    /**
     * 理货id
     */
    private Long tallyId;

    /**
     * 计划id
     */
    private Long planId;

    /**
     * 垛位
     */
    private String storageYardNm;
}
