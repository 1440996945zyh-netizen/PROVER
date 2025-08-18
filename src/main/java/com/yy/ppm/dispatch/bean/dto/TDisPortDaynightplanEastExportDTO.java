package com.yy.ppm.dispatch.bean.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TDisPortDaynightplanEastExportDTO {

    private String businessNo;

    private String scn;

    private String packingName;

    private String tradeType;

    private String planType;

    private BigDecimal remainCount;

    private BigDecimal allPlanCount;

    private BigDecimal weighCount;

    private BigDecimal remainPlanCount;

    private String massNamesTargetLabel;

    private String createByName;

    private String createTime;

    private String examineByName;

    private String examineTime;


    private String shipName;

    private String cargoOwnerName;

    private String cargoName;

    private String noticeType;

    private BigDecimal planTon;

    //计划开始时间-计划结束时间
    private String startEndPlanTime;
}
