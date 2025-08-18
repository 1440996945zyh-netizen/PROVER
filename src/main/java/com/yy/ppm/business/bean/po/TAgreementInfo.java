package com.yy.ppm.business.bean.po;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther linqi
 * @Description 合同信息表
 * @Date 2023-10-14 10:18
 */
@Setter
@Getter
public class TAgreementInfo {

    private Long id;
    private String amCode;
    private Integer amType;
    private String cusCode;
    private String cusName;
    private String goodsType;
    private String goodsCode;
    private String goodsName;
    private BigDecimal amAmount;
    private Date amDate;
    private Integer amStatus;
    private Integer isPieceGoods;
    private String receiveUnit;
    private String SCN;
    private String shipNo;
    private String shipName;
    private BigDecimal planSumAmount;
    private BigDecimal unPlanAmount;
    private String creator;
    private Date createTime;
    private Integer delete_flag;
    private String shipment;
    private String tradeType;
    private String unit;
    private String company;
    private Integer oldAmId;
    private BigDecimal backAmount;
    private Integer isFinished;
    private String companyChangeOper;
    private Date companyChangeTime;
    private String companyChangeMemo;
    private String contract_item_id;
    private String takers;
}
