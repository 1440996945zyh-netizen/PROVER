package com.yy.ppm.equipment.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 设备维修作业工时反馈子表PO
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EMaintHourFeedbackPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 维保派工主表ID
     */
    private Long maintInfoId;

    /**
     * 维修人员ID
     */
    private Long maintUserId;

    /**
     * 维修人员姓名
     */
    private String maintUserName;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date endTime;

    /**
     * 工时
     */
    private BigDecimal workHour;

    /**
     * 工作内容
     */
    private String workContent;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标识
     */
    private Integer delFlag;

    /**
     * 删除人ID
     */
    private Long deleteBy;

    /**
     * 删除人姓名
     */
    private String deleteByName;

    /**
     * 删除时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deleteTime;
}
