package com.yy.ppm.equipment.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class MaintainPlanPO extends BasePO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 计划类型 1:润滑计划 2:保养计划
     */
    private String planType;
    /**
     * 设备小类
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipSmallCategoryId;
    /**
     * 设备小类
     */
    private String equipSmallCategoryName;
    /**
     * 设备机构ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipInstitutionId;
    /**
     * 设备机构
     */
    private String equipInstitutionName;
    /**
     * 设备部件
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipUnitId;
    /**
     * 设备部件
     */
    private String equipUnitName;
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
     * 类型 1：天，2：周，3：月，4：年
     */
    private String equipType;
    /**
     * 初始日期
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date initialDate;
    /**
     * 截止日期
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date deadlineDate;
    /**
     * 最近生成任务日期
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date recentlyTaskDate;
    /**
     * 设置日期
     */
    private String setDate;
    /**
     * 是否单次 1：是，2：否
     */
    private String isSingle;
    /**
     * 周期（天数）
     */
    private String cycle;
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
     * 点检标准ID
     */
    private String standardId;
    /**
     * 点检内容
     */
    private String content;
    /**
     * 点检标准
     */
    private String standard;
    /**
     * 点检时限
     */
    private String timeLimit;
    /**
     * 初始数据 （台时、里程、吨数）
     */
    private BigDecimal initialNumber;
    /**
     * 截止数据 （台时、里程、吨数）
     */
    private BigDecimal deadlineNumber;
    /**
     * 状态 0未提报 1已提报
     */
    private Integer status;

    /**
     * 点检标准
     */
    private List<MaintainPlanItemPO> itemList;
}
