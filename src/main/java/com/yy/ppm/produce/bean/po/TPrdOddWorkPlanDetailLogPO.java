package com.yy.ppm.produce.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class TPrdOddWorkPlanDetailLogPO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 零工计划id
     */
    private Long oddPlanId;

    /**
     * 填报开始时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date reportStartTime;
    /**
     * 填报结束时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date reportEndTime;
    /**
     * 作业时长
     */
    private BigDecimal duration;

    /**
     * 填报备注
     */
    private String remark;

    private Long operateId;

    /**
     * 类型，0 修改前；1 修改后
     */
    private String logType;

    private Long parentId;

    /**
     * 台时计时-开始
     */
    private BigDecimal hourMeterStart;
    /**
     * 台时计时-结束
     */
    private BigDecimal hourMeterEnd;

}

