package com.yy.ppm.statement.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.page.PageParameter;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class StorageDemoDTO  extends PageParameter {
    private Long id;
    private String workType;
    private String cargoCategoryName;
    private String cargoTypeName;
    private String cargoName;
    private String cargoCode;
    private String source;
    private String flowDirection;
    private String inOutType;
    private String tradeType;
    private String cargoOwnerName;
    private Long   cargoOwnerId;
    private String cargoInfoNo;
    private Long   cargoInfoId;
    private String shipName	;
    private String scn;
    private String isClear;
    private Integer freeDays ;
    private String overDays ;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date   calStartTime;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date   calEndTime;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date   overDate;
    private String overTime;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date   cargoInfoTime;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date   companyTime;
    private String companyName;
    private String amount;
    private String remark;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date   createTime;
    private String settleStatus;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date   beginDate;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date   endDate;
    //是否超期
    private  String isOverTime;
}
