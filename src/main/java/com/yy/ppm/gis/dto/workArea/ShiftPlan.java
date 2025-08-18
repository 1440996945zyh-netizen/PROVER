package com.yy.ppm.gis.dto.workArea;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author linqi
 * @Description
 * @Date 2023-06-16 10:09
 */
@Setter
@Getter
public class ShiftPlan {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 昼夜计划id
     */
    private Long daynightPlanId;

    /**
     * 作业日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date workDate;

    /**
     * 作业班次
     */
    private String workShift;

    /**
     * 作业班次label
     */
    private String workShiftLabel;

    /**
     * 作业过程编码
     */
    private String processCode;

    /**
     * 作业过程名称
     */
    private String processName;

    /**
     * 作业工艺id
     */
    private Long craftId;

    /**
     * 作业场地编码
     */
    private String workStorageCode;

    /**
     * 作业场地名称
     */
    private String workStorageName;

    /**
     * 作业垛位编码 多个用,分割
     */
    private String workStackCode;

    /**
     * 作业垛位名称 多个用,分割
     */
    private String workStackName;

    /**
     * 作业舱口号
     */
    private String workHatchNo;

    /**
     * 计划吨数
     */
    private BigDecimal planWeight;

    /**
     * 计划件数
     */
    private Integer planPcs;

    /**
     * 指导员 多个,分割
     */
    private String instructor;

    /**
     * 理货员 多个,分割
     */
    private String tallyMan;

    /**
     * 备注
     */
    private String remark;

    /**
     * 计划车数
     */
    private Integer carNum;

    /**
     * 状态
     */
    private String status;

    /**
     * 状态label
     */
    private String statusLabel;

    /**
     * 开工时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date workBegin;

    /**
     * 收工时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date workEnd;

    /**
     * 昼夜计划id
     */
    private Long dayNightPlanId;

    /**
     * 船名航次id
     */
    private String vesselVisitId;

    /**
     * 船名
     */
    private String vesselName;

    /**
     * 航次
     */
    private String voyage;

    /**
     * 泊位编码
     */
    private String berthCodeCur;

    /**
     * 泊位名称
     */
    private String berthName;

    /**
     * 货名编码
     */
    private String cargoCode;

    /**
     * 货名
     */
    private String cargoName;
}
