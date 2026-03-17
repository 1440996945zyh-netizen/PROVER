package com.yy.ppm.equipment.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class InspectionPlanTaskPO extends BasePO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 点检计划ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipPlanId;
    /**
     * 设备ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipId;
    /**
     * 设备NAME
     */
    private String equipName;
    private String timeLimit;

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

    /**
     * 初始数据 （台时、里程、吨数）
     */
    private BigDecimal initialNumber;
    /**
     * 截止数据 （台时、里程、吨数）
     */
    private BigDecimal deadlineNumber;
    /**
     * 点检员ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long inspectorId;
    /**
     * 点检员NAME
     */
    private String inspectorName;
    /**
     * 设备小类
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipSmallCategoryId;
    /**
     * 设备小类
     */
    private String equipSmallCategoryName;
    private String equipType;
    private String status;

}
