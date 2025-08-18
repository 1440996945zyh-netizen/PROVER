package com.yy.ppm.appWork.bean.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 木材理货港存查询dto
 * @author chenfs
 * @date 2023-09-18
 */
@Getter
@Setter
@ToString
public class TYardMeasureSearchDTO extends BasePO {


    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal planId;//计划id
    private String  storehouseName;//库场名称
    private String  stackPositionName;
    private String stackPosition;//垛位号
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal  storehouseId;//库场Id
    private String seqNo;//序号
    private BigDecimal shipvoyageId; //航次ID
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal loadingListId; //ID
    private String cabinNo; //舱口
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal  cargoInfoId;//票货Id
    private Long trustId;//指令id
    private String heatNo;//炉号
    private String mark;//标记
    private String trustIds;
    private String processCode;
    private String isFrontier;
    private String switchIsFrontier;
    private String workPlanId;


}

