package com.yy.ppm.common.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class ResponsePopupTrustDTO {

    private Long id;
    private Long companyId;
    private String companyName;
    private String trustNo;
    private String processCode;
    private String processName;
    private String customerName;
    private String settlementBasisCode;
    private String settlementBasisName;
    private String remark;
    private Long shipvoyageId;
    private Long shipvoyageItemId;
    private String loadUnload;
    private String voyage;
    private Long berthId;
    private String berthName;
    private String cars;
    private String planQuantity;
    private String planTon;
    private String cargoOwnerName;
    private String cargoAgentName;
    private String cargoName;
    private String type;
    private String releaseTime;
    //回显船名航次
    private String shipNameVoyages;
    //回显场地安排
    private String massNamesTarget;
  /*  private List<String> statementStatusList;*/
    // 计划开始时间
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    // 计划结束时间
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private String shipStatusCode;
    private String shipStatusName;
    //统计是否完货
    private int idCount;
    private int clearNumber;
    private int isClear;

    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
