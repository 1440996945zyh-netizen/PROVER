package com.yy.ppm.tallyExtrinsic.bean.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 木材理货记录查询dto
 * @author chenfs
 * @date 2023-04-25
 */
@Getter
@Setter
@ToString
public class TallyRecordSearchDTO extends BasePO {


    @JsonSerialize(using = ToStringSerializer.class)
    private Long planId;//计划id
    private String  processCode;//库场名称
    private String equipmentNo; //机械名称
    private String transportEquipmentNo; //车号
    private String customerItemName; //车号

    private Long cargoAgentId;
    private Long id;
    private Long tallyId;
    private String cargoAgentName;
    private Long cargoOwnerId;
    private String cargoOwnerName;
    private String isDzy; //是否待作业
    private Long trustId;
    private Long cargoInfoId;
    /**
     * 垛位号
     */
    private String stackPosition;
    private String cargoName;
    //时间范围查询使用
    private String startTime;
    private String endTime;
    private String billNo;
    private String createName;
    private String cargoInfoNo;
    private String classCode;
    private String className;
    private String workDate;


}

