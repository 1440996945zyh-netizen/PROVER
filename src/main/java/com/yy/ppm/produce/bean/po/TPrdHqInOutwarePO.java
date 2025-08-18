package com.yy.ppm.produce.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class TPrdHqInOutwarePO extends BasePO implements Serializable {
    /**
     * 海清data id
     */
    private Long id;
    /**
     * 作业时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 货主
     */
    private String cargoOwnerName;
    /**
     * 计划号
     */
    private String planNo;
    /**
     * 工作区域
     */
    private String cargoInfoNo;


    private String cargoName;

    private String pqNo;

    private String quantity;

    private String ton;

    private String hqLength;

    private String width;

    private String height;

    private String volume;
    //区域
    private String stackPositionName;
    //海清垛位
    private String yardName;

    //车牌号
    private String transportEquipmentNo;

    private String weightSelf;

    private String weightAll;

    private String weightGoods;
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date weighInDt;

    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date weighOutDt;

    private String createByName;

    private String status;
    private String hqCargoName;
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date inPortTime;
    private String inPortName;
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date outPortTime;
    private String outPortName;
    private String inShipVoyage;
    private String outShipVoyage;
}
