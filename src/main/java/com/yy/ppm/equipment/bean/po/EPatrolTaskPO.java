package com.yy.ppm.equipment.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 巡检任务表 PO
 *
 * @author system
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EPatrolTaskPO extends BasePO {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /** 计划ID */
    private String planId;

    /** 巡检路线ID */
    private String routeId;

    /** 巡检员ID */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long patrolId;

    /** 巡检员NAME */
    private String patrolName;

    /** 开始日期 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    /** 结束日期 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    /** 巡检状态 0未检 1进行中 2已检 */
    private Integer status;

    /** 巡检路线编码 */
    private String routeCode;

    /** 巡检路线名称 */
    private String routeName;

    /** 巡检路线等级 */
    private String routeLevel;

    /** 巡检计划编码 */
    private String planCode;

    /** 计划名称 */
    private String planName;
}
