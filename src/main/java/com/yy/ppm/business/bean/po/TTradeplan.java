package com.yy.ppm.business.bean.po;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-10-14 16:20
 */
@Setter
@Getter
public class TTradeplan {

    private Long id;
    private String planCode;
    private String amCode;
    private BigDecimal planAmount;
    private Integer disable;
    private Integer planCtrlType;
    private Integer planType;
    private Integer planCompStatus;
    private String taker;
    private Integer port;
    private String portName;
    private String creator;
    private Date createTime;
    private String editor;
    private Date editTime;
    private Integer delete_flag;
    private String uploadType;
    private String unAgreeReason;
    private BigDecimal plan_limit;
    private String memo;
    private Integer isLimit;
    private Integer amId;
    private String tpmemo;
    private String isNotice;
    private String wbCode;
    private BigDecimal completedAmount;
    private Integer oldPlanCompStatus;
    private String contract_item_id;
}
