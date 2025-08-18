package com.yy.ppm.statement.bean.dto.costShipWaterElectricity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-23 11:39
 */
@Setter
@Getter
public class TBusTrustQueryDTO {

    /**
     * 船舶预报ID
     */
    private Long shipvoyageItemId;

    /**
     * 货代ID
     */
    private Long customerId;

    /**
     * 船舶状态编码
     */
    private String shipStatusCode;

    /**
     * 离泊时间起始
     */
    @DateTimeFormat(pattern = "yy-MM-dd")
    private Date beginLeaveBerthTime;

    /**
     * 离泊时间截止
     */
    @DateTimeFormat(pattern = "yy-MM-dd")
    private Date endLeaveBerthTime;

    /**
     * 指令状态
     */
    private String status;

    /**
     * 结算状态 10未结算/20已结算
     */
    private String statementStatus;
    /**
     * 离泊时间起始
     */
    @DateTimeFormat(pattern = "yy-MM-dd")
    private Date beginLeavePortTime;

    /**
     * 离泊时间截止
     */
    @DateTimeFormat(pattern = "yy-MM-dd")
    private Date endLeavePortTime;
    /**
     * 是否离港 查询条件
     */
    private String isLeavePort;
    /**
     * 船名航次查询
     */
    private String shipName;
    private String voyage;

    /**
     * 是否中作业区
     */
    private String isCentre;
    /**
     * 作业公司
     */
    private String companyId;
}
