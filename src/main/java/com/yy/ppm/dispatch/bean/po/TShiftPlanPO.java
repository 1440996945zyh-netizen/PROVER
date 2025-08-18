package com.yy.ppm.dispatch.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.validate.AddGroup;
import com.yy.common.validate.EditGroup;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 工班计划主表Bo
 * @author yangcl*/
@Data
public class TShiftPlanPO implements Serializable {
    /**
     * 主键ID
     */
    @NotNull(message = "昼夜计划ID不可为空",groups = EditGroup.class)
    private Long id;

    /**
     * 昼夜计划ID
     */
    @NotNull(message = "昼夜计划ID不可为空",groups = {AddGroup.class, EditGroup.class})
    private Long daynightPlanId;

    /**
     * 工班日期
     */
    @NotNull(message = "工班日期不可为空",groups = {AddGroup.class})
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date workDate;

    /**
     * 班次 字典WORK_SHIFT
     */
    @NotNull(message = "班次不可为空",groups = {AddGroup.class})
    private String workShift;

    /**
     * 作业过程
     */
    @NotNull(message = "作业过程不可为空")
    private String workProcessCode;

    /**
     * 作业工艺ID
     */
    private Long craftId;

    /**
     * 作业场地code
     */
    private String workStorageCode;

    /**
     * 作业垛位CODE 多个用,分割
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
    @NotBlank(message = "指导员不可为空")
    private String instructor;

    /**
     * 理货员 多个,分割
     */
    @NotBlank(message = "理货员不可为空")
    private String tallyMan;

    /**
     * 备注
     */
    private String remark;

    /**
     * 计划车数
     */
    private Long carNum;

    /**
     * 状态 字典WORK_STATUS
     */
    private String status;

    /**
     * 开工时间
     */
    private Date workBegin;

    /**
     * 收工时间
     */
    private Date workEnd;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private Long updateBy;

    /**
     * 修改时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}

