package com.yy.ppm.produce.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class TDisShipVoyageDTO {

    /**
     * 主键ID
     */
    private Long id;
    private String scn;
    private Long shipId;
    private String shipName;
    private String impExp;
    private String tradeType;
    private String loadUnload;
    private Long shipVoyageId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date workEndTime;

    /**
     * 靠泊时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date berthTime;

    /**
     * 靠泊泊位ID
     */
    private Long berthId;

    /**
     * 历史泊位名称
     */
    private String berthName;

    /**
     * 整船是否完工(手动 0未完工  1已完工)
     */
    private String isShipClear;

    /**
     * 人员审核id
     */
    private Long shipPersonExamineBy;

    /**
     * 人员审核姓名
     */
    private String shipPersonExamineByName;

    /**
     * 人员审核时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date shipPersonExamineTime;

    /**
     * 机械审核id
     */
    private Long shipMacExamineBy;

    /**
     * 机械审核姓名
     */
    private String shipMacExamineByName;

    /**
     * 机械审核时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date shipMacExamineTime;


    /**
     * 舷靠,左舷、右舷
     */
    private String berthType;


    /**
     * 离泊时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date leaveBerthTime;

    /**
     * 离港时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date leavePortTime;


    private String remark;

    private String shipStatusCode;

    /**
     * 状态名称 ，预报、接收、抵锚..,（字典SHIPSTATUS）
     */
    private String shipStatusName;
    private String voyage;
    /**
     * 码头
     */
    private String wharf;

    private String cargoName;

    private Long shipVoyageItemId;

    //整船调整状态 20 已调整
    private String status;
    private String statusLabel;
    private String statusLaborLabel;

}
