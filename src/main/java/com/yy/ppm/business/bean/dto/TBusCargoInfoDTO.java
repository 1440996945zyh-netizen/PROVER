package com.yy.ppm.business.bean.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.util.str.StringUtil;
import com.yy.ppm.business.bean.po.TBusCargoInfoPO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName 票货信息表(TBusCargoInfo)DTO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月03日 18:47:00
 */
@Data
public class TBusCargoInfoDTO extends TBusCargoInfoPO {

    private static final long serialVersionUID = 216082502981673447L;

    private String workType;

    private Long cargoInfoId;

    private String shipNameVoyage;

    //理货方式
    private String tally;

    private Integer portStorageQuantity;

    private BigDecimal portStorageTon;

    private String contractItemId;
    /**老系统合同代码*/
    private String contractCode;

    private String businessNo;

    private BigDecimal handoverlistTon;

    private BigDecimal trustCargoTon;

    private BigDecimal weightGoods;

    private BigDecimal balanceTon;

    private BigDecimal balancePortStorageTon;

    /**
     * 预缴状态
     */
    private String prePayStatus;
    /**
     * 预缴编号
     */
    private String prePayNo;

    private String cargoInfoName;
    /**
     * 进出口
     */
    private String impExp;
    /**
     * 装卸船
     */
    private String loadUnload;
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
    /**
     * 靠泊时间
     */
    private Date berthTime;

    /**
     * 浮动量(‰)
     */
    private Integer floatTon;

    private String type;

    private String trustNos;

    private String releaseByNames;

    private String releaseTimes;

    //已完成量
    private BigDecimal weightGoodsJG;
    private BigDecimal weightGoodsSG;
    //剩余量
    private BigDecimal balanceTonJG;
    private BigDecimal balanceTonSG;
    //下发量
    private BigDecimal trustCargoTonJG;
    private BigDecimal trustCargoTonSG;




    private String massId;

    private String cargoCode;

    //免堆存期
    private Integer freeStorageDays;

    //堆存时长
    private String day;

    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    /**
     * 放行货物备注
     */
    private String releaseRemark ;
    /**
     * 是否放行
     */
    private String isRelease ;
    /**
     * 是否预缴
     */
    private String isPrePay ;


    private String isSettleStorage;

    private String overdueDays;

    private Integer overdueDaysInt;

    private String statementStatus;

    private Integer orderNum;

    private String isHq;


    public void setOverdueDays(String overdueDays) {
        this.overdueDays = overdueDays;
        if(StringUtil.isNotEmpty(overdueDays)){
            this.overdueDaysInt = Integer.valueOf(overdueDays);
        }
    }
}
