package com.yy.ppm.statement.bean.dto.costShip;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-20 14:03
 */
@Setter
@Getter
public class TDisShipvoyageItemDTO {

    private Long id;

    private String scn;

    private Long shipvoyageItemId;

    private String companyName;

    private String shipName;

    private String voyage;

    private String tradeType;

    private String loadUnload;

    private String customerName;

    private BigDecimal netWeight;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date berthTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date leaveBerthTime;

    private BigDecimal berthDays;
    private BigDecimal berthHours;

    private String statementNo;

    private BigDecimal amount;

    private String createByName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String status;

    private String berthName;

    private String arrivalAnchorageTime;

    private String anchorageDays;

    private String tinggongFugongTimes;

    private Integer tinggongFugongHours;

    private String companyId;

    private String shipvoyageId;

    private String customerId;

    /**
     * 导出账单在用 SHIP_NAME_EXPORT
     */
    private String shipNameExport;

    /**
     * 船舶状态
     */
    private String shipStatusCode;
    /**
     * 泊位对应的作业区域
     */
    private String workAreaCd;

    private BigDecimal paymentAmount;
    private BigDecimal paymentAmountBack;


    private String dynamicTypeCode;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dynamicStartTime;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dynamicEndTime;

    private String rateItemCode;

}
