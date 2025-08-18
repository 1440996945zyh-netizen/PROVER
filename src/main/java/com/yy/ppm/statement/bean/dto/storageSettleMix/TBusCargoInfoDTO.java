package com.yy.ppm.statement.bean.dto.storageSettleMix;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @java.util.Date 2023-11-24 14:05
 */
@Setter
@Getter
public class TBusCargoInfoDTO {

    private Long id;

    private String cargoInfoNo;

    private Long companyId;

    private String companyName;

    private Long shipvoyageId;

    private Long shipvoyageItemId;

    private String shipNameVoyage;

    private String scn;

    private Long trustId;

    private String trustNos;

    private Long cargoOwnerId;

    private String cargoOwnerName;

    private String workType;

    private String workTypeLabel;

    private String cargoCode;

    private String cargoName;

    private String tradeType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String berthTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String leaveBerthTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String leavePortTime;

    private String releaseTimes;

    private String loadUnload;

    private String impExp;

    private String impExpLabel;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date workEndTime;

    /**
     * 超期天数
     */
    private Integer overdueDays;

    private String isClear;

    private String clearByName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date clearDate;

    /**
     * 混配时间
     */
    private Date mixTime;

    private BigDecimal ton;
}
