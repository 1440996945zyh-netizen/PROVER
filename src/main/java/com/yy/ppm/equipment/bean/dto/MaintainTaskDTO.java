package com.yy.ppm.equipment.bean.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class MaintainTaskDTO extends BasePO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 点检计划ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipPlanId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipSmallCategoryId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipInstitutionId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipUnitId;

    /**
     * 设备ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipId;
    /**
     * 设备NAME
     */
    private String equipName;

    /**
     * 开始日期
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    /**
     * 结束日期
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    private String equipType;
    private String startTime;
    private String endTime;
    private String status;
    private String planType;
    private Long inspectorId;
}
