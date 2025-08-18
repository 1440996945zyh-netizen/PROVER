package com.yy.ppm.statement.bean.dto.costShipWaterElectricity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-23 11:40
 */
@Setter
@Getter
public class TBusTrustDTO {

    private Long id;

    private Long companyId;

    private String companyName;

    private Long customerId;

    private String customerName;

    private String processCode;

    private String processName;

    private String shipNameVoyage;

    private String berthName;

    private String tradeType;

    private BigDecimal netWeight;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date berthTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date leaveBerthTime;

    private String statementNo;

    private String createByName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String status;

    private String statusLabel;

    private String statementStatus;

    private Long shipvoyageId;

    private Long shipvoyageItemId;

    private String shipCustomerId;

    private String shipCustomerName;

    private String workAreaCd;

}
