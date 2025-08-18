package com.yy.ppm.statement.bean.dto.storageFee;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @java.util.Date 2023-11-24 14:05
 */
@Setter
@Getter
public class TBusCargoInfoQueryDTO {

    /**
     * 船舶航次ID
     */
    private Long shipvoyageItemId;

    /**
     * 货主ID
     */
    private Long cargoOwnerId;

    /**
     * 票货号
     */
    private String cargoInfoNo;

    /**
     * 船舶状态编码
     */
    private String shipStatusCode;

    /**
     * 货名
     */
    private String cargoName;

    /**
     * 离泊时间起始
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date beginLeaveBerthTime;

    /**
     * 离泊时间截止
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endLeaveBerthTime;

    /**
     * 是否超期编码
     */
    private String isOverdue;

    /**
     * 是否完货编码
     */
    private String isClear;

    /**
     * 进出口编码
     */
    private String impExp;

    /**
     * SCN
     */
    private String scn;

    /**
     * 合同编号
     */
    private String contractCode;
    /**
     * 作业模式
     */
    private String workType;
    /**
     * 票货的结算状态
     */
    private String statementStatus;

    /**
     * 是否计算 其他否/1是
     */
    private String isCalculate;
    /**
     * 金额是否为零
     */
    private String isZero;

    /**
     * 作业公司
     */
    private String companyId;

    /**
     *
     */
    private String routeId;

    private String shipName;

    private String voyage;
    private String isReduceTypeThree;
}
