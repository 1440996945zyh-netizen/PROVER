package com.yy.ppm.produce.bean.dto.workTicket;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@ToString
public class PoundToPortStorageDTO {

    private Long tallyId;
    private Long planId;
    private Long tallyItemId;
    private Long cargoInfoId;
    private String cargoInfoNo;
    private String planNo;
    private String processCode;
    private String processName;
    private String transportEquipmentId;
    private String transportEquipmentNo;
    private String weighbridgeId;
    private String tallyStatus;
    private String delFlag;
    private String cargoCode;
    private String cargoName;
    private Integer quantity;
    private BigDecimal ton;
    private String locationId;
    private String locationNo;
    private String stackPositionName;
    private String trustCargoInfoId;
    private String sourceCd;
    private String sourceNm;
    private String targetCd;
    private String targetNm;
    private String sourceOrTargetFlag;

    private Long storehouseId;
    private String storehouseName;
    private Long regionId;
    private String regionName;
    private Long massId;
    private String massName;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date workDate;
    private String classCode;
    private String className;
    private String portStorageDetailId;
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date weighInDt;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date weighOutDt;
    /**
     * 磅单编号/检斤号
     */
    private String unionNo;

    private Long companyId;
    private String companyName;
    private String trustNo;
    private String trustId;
    private String truckPlate;



}
