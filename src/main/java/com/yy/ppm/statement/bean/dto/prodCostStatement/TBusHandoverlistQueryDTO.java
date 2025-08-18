package com.yy.ppm.statement.bean.dto.prodCostStatement;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-07 11:03
 */
@Setter
@Getter
public class TBusHandoverlistQueryDTO {

    /**
     * 航次ID
     */
    private Long shipvoyageItemId;

    /**
     * 货主ID
     */
    private Long cargoOwnerId;

    /**
     * 结算状态（字典：STATEMENT_STATUS）
     */
    private String statementStatusCode;

    /**
     * 类型（1.装卸船清单 2.陆集陆疏）
     */
    private String type;

    /**
     * 状态CODE，预报、接收、抵锚..,（字典SHIP_STATUS）
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
     * 是否离港 1108 查询条件isLeave   1 离港，2 未离港
     */
    private String isLeave;

    /**
     * 结算状态  1108 查询条件status
     */
    private String status;

    private String cargoName;

    private String cargoInfoNo;

    private String scn;
    private String shipName;
    private String voyage;
    private String workType;
    private String isClear;

    /**
     * 作业公司
     */
    private String companyId;
}
