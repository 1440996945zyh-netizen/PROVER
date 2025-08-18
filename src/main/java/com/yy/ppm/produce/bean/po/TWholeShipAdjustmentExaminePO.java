package com.yy.ppm.produce.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Setter
@Getter
public class TWholeShipAdjustmentExaminePO extends BasePO{
    /**
     * 船舶子表id
     */
    private Long shipvoyageItemId;

    /**
     * 人员审核id
     */
    private Long shipPersonExamineBy;

    /**
     * 人员审核姓名
     */
    private String shipPersonExamineByName;

    /**
     * 人员审核时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date shipPersonExamineTime;

    /**
     * 机械审核id
     */
    private Long shipMacExamineBy;

    /**
     * 机械审核姓名
     */
    private String shipMacExamineByName;

    /**
     * 机械审核时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date shipMacExamineTime;

    //整船是否完工(手动 0未完工  1已完工)
    private String isShipClear;
}
