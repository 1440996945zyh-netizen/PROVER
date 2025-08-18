package com.yy.ppm.statement.bean.dto.costShip;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-20 14:01
 */
@Setter
@Getter
public class TDisShipvoyageItemQueryDTO {

    /**
     * 船舶航次ID
     */
    private Long shipvoyageItemId;

    /**
     * 货代ID
     */
    private Long customerId;

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
     * 结算状态 10未结算/20已结算
     */
    private String status;

    /**
     * SCN
     */
    private String scn;

    /**
     * 是否离港 0否/1是
     */
    private String isLeavePort;
    /**
     * 船舶主表id
     */
    private Long shipvoyagId;

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
     * 作业区域
     */
    private String workAreaCd;

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
