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
public class TPrdOddSaveDTO extends TPrdOddWorkPlanPO {

    private List<Long> ids;

    /**
     * 填报人
     */
    private Long reportUserId;
    /**
     * 填报人
     */
    private String reportUserName;
    /**
     * 填报时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reportTime;

    /**
     * 计划详情
     */
    private List<TPrdOddWorkPlanDetailPO> workTimeTable;
}
