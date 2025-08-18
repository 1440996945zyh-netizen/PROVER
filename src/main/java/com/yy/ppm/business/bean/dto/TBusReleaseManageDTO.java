package com.yy.ppm.business.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.business.bean.po.TBusCargoInfoPO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class TBusReleaseManageDTO extends TBusCargoInfoPO {

    private String tfbcpId;
    private String prepaymentCode;
    private String prepaymentStatus;

    private String workType;

    private Long cargoInfoId;

    private String shipNameVoyage;

    //理货方式
    private String tally;

    private String contractItemId;
    /**老系统合同代码*/
    private String contractCode;

    private String businessNo;

    private String cargoInfoName;

    /**
     * 贸别
     */
    private String tradeType;
    /**
     * 航次
     */
    private String voyage;
    /**
     * 通知单类型（卸船、集港）
     */
    private String trustType;

    private String type;

    private String trustNos;

    private String releaseByNames;

    private String releaseTimes;

    private String cargoCode;

    private String isPrePay;

    private String isRelease;

    private String releaseRemark;

    private List<Long> fileIds;

}
