package com.yy.ppm.equipment.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 设备维修操作日志 PO
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class EMaintInfoLogPO extends BasePO {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 维修主表ID */
    private Long maintInfoId;

    /** 工单号 */
    private String workOrderNo;

    /** 动作编码 */
    private String actionCode;

    /** 动作名称 */
    private String actionName;

    /** 操作前状态 */
    private Integer fromStatus;

    /** 操作后状态 */
    private Integer toStatus;

    /** 操作人ID */
    private Long operateBy;

    /** 操作人姓名 */
    private String operateByName;

    /** 操作时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operateTime;

    /** 备注 */
    private String remark;

    /** 操作快照JSON */
    private String snapshotJson;

    /** 删除标识 */
    private Integer delFlag;

    /** 删除人ID */
    private Long deleteBy;

    /** 删除人姓名 */
    private String deleteByName;

    /** 删除时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deleteTime;
}