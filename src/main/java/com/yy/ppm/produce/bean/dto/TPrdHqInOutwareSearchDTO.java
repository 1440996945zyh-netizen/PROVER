package com.yy.ppm.produce.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.page.PageParameter;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class TPrdHqInOutwareSearchDTO extends PageParameter implements Serializable {
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

    private String cargoInfoNo;


    private String cargoName;

    private String pqNo;

    private String quantity;

    private String hqLength;

    private String width;

    private String height;

    private String volume;
    //海清垛位
    private String yardName;
    //车牌号
    private String transportEquipmentNo;

    private String weightSelf;

    private String weightAll;

    private String weightGoods;

    private String weighInDt;

    private String weighOutDt;

    private String createByName;

    private String status;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date beginDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
}
