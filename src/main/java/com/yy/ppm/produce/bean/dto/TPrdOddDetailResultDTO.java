package com.yy.ppm.produce.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.produce.bean.po.TPrdOddWorkPlanDetailPO;
import com.yy.ppm.produce.bean.po.TPrdOddWorkPlanPO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Auther wangxd
 * @Description
 * @Date 2023-12-12 10:08
 */
@Setter
@Getter
public class TPrdOddDetailResultDTO extends TPrdOddWorkPlanDetailPO {

    /**
     * 填报开始时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date startTime;

    /**
     * 填报结束时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date endTime;

    /**
     * 零工单号
     */
    private String oddPlanNo;

    private String reportTime;
}
