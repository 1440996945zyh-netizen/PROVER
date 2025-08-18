package com.yy.ppm.common.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TDisPoundInfoDTO {

    private String type;

    /**榜单号*/
    private String unionNo;
    /**进港日期时间*/
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date weighInDt;
    /**出港日期时间*/
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date weighOutDt;
    /**进港司磅员*/
    private String checkerInName;
    /**出港司磅员*/
    private String checkerOutName;
    /**合同号*/
    private String invNo;
    /*** 计划单号*/
    private String planNo;
    /*** 货物名称*/
    private String goodsName;
    private String goodsDes;
    /**车牌号*/
    private String truckPlate;
    private BigDecimal weighNumberype;
    private BigDecimal weighOuttype;
    private BigDecimal weightSelf;
    private BigDecimal weightGoods;
    /**船名*/
    private String comName;
    /**一次磅*/
    private String inBangNo;
    /**二次磅*/
    private String outBangNo;
    /**毛重*/
    private String weightAll;
    private String invRem;
    private String agentName;
    private String tsptId;
    private String workErweiId;
    private String idNumber;
    private String cargoOwnerName;
    private String portCode;
    private String scn;
    private String goodsCode;
    private String tradeType;
    private String driver;
    private String tel;
    //物流公司名称
    private String consigneeName;
    //货主公司
    private String consignorName;
    private String voyage;
    private String quantity;
    private String hw;
    private String cargoInfoNo;
    private String trustNo;
    private String zgsc;
    /**
     * 理货开始时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date workTimeStart;

    /**
     * 理货结束时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date workTimeEnd;
    private String massName;
    private String lhsc;
    private String lhq;
    private String lhh;
    private String 车辆排放标准;

}
