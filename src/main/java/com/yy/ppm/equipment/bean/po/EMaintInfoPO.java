package com.yy.ppm.equipment.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 设备维修派工信息 PO
 *
 * @author system
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EMaintInfoPO extends BasePO {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 设备ID */
    private Long equipId;

    /** 故障发现时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date faultFindTime;

    /** 紧急程度 */
    private String emergencyLevel;

    /** 维修类型代码 */
    private String maintTypeCode;

    /** 维修类型名称 */
    private String maintTypeName;

    /** 是否停机 */
    private Integer isStopped;

    /** 故障描述 */
    private String faultDesc;

    /** 提报类型代码 */
    private String reportTypeCode;

    /** 提报类型名称 */
    private String reportTypeName;

    /** 派工类型代码 */
    private String dispatchTypeCode;

    /** 派工类型名称 */
    private String dispatchTypeName;

    /** 承修单位ID */
    private Long maintOrgId;

    /** 承修单位名称 */
    private String maintOrgName;

    /** 维修负责人ID */
    private String maintLeaderId;

    /** 维修负责人姓名 */
    private String maintLeaderName;

    /** 维修负责人手机号 */
    private String maintLeaderMobile;

    /** 是否涉及特殊作业 */
    private String isSpecialJob;

    /** 特殊作业情况代码 */
    private String specialJobCode;

    /** 特殊作业情况名称 */
    private String specialJobName;

    /** 派工人ID */
    private Long dispatcherId;

    /** 派工人姓名 */
    private String dispatcherName;

    /** 派工时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dispatchTime;

    /** 维修开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date maintStartTime;

    /** 维修结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date maintEndTime;

    /** 验收人ID */
    private Long accepterId;

    /** 验收人姓名 */
    private String accepterName;

    /** 验收时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date acceptanceTime;

    /** 验收备注 */
    private String acceptanceRemark;

    /** 是否通过验收（0-否，1-是） */
    private Integer isAccepted;

    /** 维修说明 */
    private String maintRemark;

    /** 工单状态 */
    private Integer status;

    /** 工单号 */
    private String workOrderNo;

    /** 作废人ID */
    private Long cancelBy;

    /** 作废人姓名 */
    private String cancelByName;

    /** 作废时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date cancelTime;

    /** 作废备注 */
    private String cancelRemark;

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

    /** 批量操作ID列表 */
    private java.util.List<Long> ids;

    /** 来源 */
    private Integer source;

    /** 来源主表ID */
    private Long sourceId;

    /** 来源子表ID */
    private Long sourceItemId;

    /** 维修项目申请单号 */
    private String mantAppNumber;
}
