package com.yy.ppm.statement.bean.dto.storageSettleMix;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-11-24 19:16
 */
@Setter
@Getter
public class VWeightInfo {

    private Integer noteId;
    private String unionNo;
    private Date weighInDt;
    private String checkerInName;
    private Date weighOutDt;
    private String checkerOutName;
    private String invNo;
    private String planNo;
    private String subPlanNo;
    private String goodsName;
    private String goodsDes;
    private String truckPlate;
    private BigDecimal weighNumberype;
    private BigDecimal weighOuttype;
    private BigDecimal weightSelf;
    private BigDecimal weightAll;
    private BigDecimal weightGoods;
    private String comName;
    private String voyage;
    private String invRem;
    private String agentName;
    private String tsptId;
    private String workErweiId;
    private String idNumber;
    private String inBangNo;
    private String outBangNo;
    private String portCode;
    private String scn;
    private String goodsCode;
    private String tradeType;
    private String driver;
    private String tel;
    private String consignorCode;
    private String consignorName;
    private String consigneeCode;
    private String consigneeName;
    private String shipName;
}
