package com.yy.ppm.appWork.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 地磅PO
 *
 * @author chenfs
 * @date 2023-11-15 20:53:36
 */

@Getter
@Setter
@ToString
public class DepartureDTO extends BasePO {
    /**ID*/
    @JsonSerialize(using = ToStringSerializer.class)
    private Long noteId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tallyId;
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
    /**车牌号*/
    private String truckPlate;
    /**船名*/
    private String comName;
    /**一次磅*/
    private String inBangNo;
    /**二次磅*/
    private String outBangNo;
    /**毛重*/
    private String weightAll;

    private String tsptId;

    private String workErweiId;
}

