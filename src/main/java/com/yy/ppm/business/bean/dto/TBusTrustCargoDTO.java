package com.yy.ppm.business.bean.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.util.str.StringUtil;
import com.yy.ppm.business.bean.po.TBusTrustCargoPO;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @ClassName (TBusTrustCargo)DTO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月05日 09:21:00
 */
@Setter
@Getter
public class TBusTrustCargoDTO extends TBusTrustCargoPO {

    private static final long serialVersionUID = -51077678172041616L;

    private String workType;

    private String cargoInfoNo;

    private Long trustCargoId;

    private String[] hatchArr;

    private String workAreaCd;

    private String workAreaNm;

    private String isCharge;

    private BigDecimal weighTon;

    private List<String> feeList;

    private String trustType;

    private String isStop;

    private String shipvoyageItemId;
    private String deliveryNumbers;
    private String permitThrough;

    private Long isStopOperationBy;
    //开启/关闭操作姓名
    private String isStopOperationName;
    //开启/关闭操作时间
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date isStopOperationTime;

    //标志位 过磅量大于计划量 若大于设置为1变红 小于设置为2不变
    private String tonFlag;

    private List<String> deliveryList;

    public void setDeliveryNumbers(String deliveryNumbers) {
        this.deliveryNumbers = deliveryNumbers;
        if(StringUtil.isNotEmpty(this.deliveryNumbers) && CollectionUtils.isEmpty(this.deliveryList)){
            this.deliveryNumbers = this.deliveryNumbers.replaceAll(",","，");
            List<String> list = Arrays.asList(this.deliveryNumbers.split("，"));
            this.deliveryList = list;
        }
    }

    /***
     *     委托单子表id
     */
    private Long entrustDetailId;
    /***
     *     委托单主表id
     */
    private Long entrustId;

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


    private String disRate;

    private String stopRemark;

}
