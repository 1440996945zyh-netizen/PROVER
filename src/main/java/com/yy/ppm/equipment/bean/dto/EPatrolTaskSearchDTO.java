package com.yy.ppm.equipment.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

/**
 * 巡检任务查询 DTO
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EPatrolTaskSearchDTO extends BasePO {

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

    /** 巡检员姓名 */
    private String patrolName;

    /** 状态 */
    private String status;

    /** 查询开始时间*/
    private String startTime;

    /** 查询结束时间 */
    private String endTime;

    /** 设备ID */
    private Long equipId;

    /** 设备名称  */
    private String equipName;
    /** 巡检路线名称 */
    private String routeName;
    /** 计划名称 */
    private String planName;
}
