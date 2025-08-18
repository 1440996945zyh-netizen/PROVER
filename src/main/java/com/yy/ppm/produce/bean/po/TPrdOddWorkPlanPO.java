package com.yy.ppm.produce.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class TPrdOddWorkPlanPO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 零工类型
     */
    private String oddType;
    /**
     * 申请作业部门
     */
    private String workDeptId;
    /**
     * 申请作业部门
     */
    private String workDeptName;
    /**
     * 机械类型编码
     */
    private String macTypeCode;
    /**
     * 机械类型名称
     */
    private String macTypeName;
    /**
     * 机械编号
     */
    private String macNo;
    /**
     * 固机队部门下的人员
     */
    private String oddUserIds;
    /**
     * 机械编号id
     */
    private String macId;
    /**
     * 机械数量
     */
    private Integer macAmount;
    /**
     * 人员数量
     */
    private Integer workerAmount;
    /**
     * 作业内容
     */
    private String workContent;
    /**
     * 填报开始时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date reportStartTime;
    /**
     * 填报结束时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date reportEndTime;
    /**
     * 作业时长
     */
    private BigDecimal workDuration;
    /**
     * 总工时
     */
    private BigDecimal workHours;
    /**
     * 确认人（一级审批）
     */
    private Long confirmBy;
    /**
     * 确认人（一级审批）
     */
    private String confirmByName;
    /**
     * 确认时间（一级审批）
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date confirmTime;
    /**
     * 审批人（二级审批）
     */
    private Long firstApproveBy;
    /**
     * 审批人（二级审批）
     */
    private String firstApproveByName;
    /**
     * 审批时间（二级审批）
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date firstApproveTime;
    /**
     * 审批人（三级审批）
     */
    private Long secondApproveBy;
    /**
     * 审批人（三级审批）
     */
    private String secondApproveByName;
    /**
     * 审批时间（三级审批）
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date secondApproveTime;
    /**
     * 状态（字典 ODD_STATUS）
     */
    private String status;
    /**
     * 填报备注
     */
    private String remark;
    /**
     * 申请人部门
     */
    private Long createFromDept;
    /**
     * 申请人部门
     */
    private String createFromDeptName;

    /**
     * 是否驳回
     */
    private String isReject;
    /**
     * 驳回人
     */
    private Long rejectBy;
    /**
     * 驳回人
     */
    private String rejectByName;
    /**
     * 驳回时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date rejectTime;
    /**
     * 驳回原因
     */
    private String rejectReason;

    /**
     * 作废人
     */
    private Long abandonedBy;
    /**
     * 作废人
     */
    private String abandonedByName;
    /**
     * 作废时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date abandonedTime;
    /**
     * 驳回原因
     */
    private String abandonedReason;
    /**
     * 审批人（四级审批）
     */
    private Long thirdApproveBy;
    /**
     * 审批人（四级审批）
     */
    private String thirdApproveByName;
    /**
     * 审批时间（四级审批）
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date thirdApproveTime;
    /**
     * 零工编号
     */
    private String oddPlanNo;
    /**
     * 班次（01白班，02夜班）
     */
    private String classType;
    /**
     * 班次日期
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date classDate;

    /**
     * 零工作业分类编码
     */
    private String oddWorkTypeCode;

    /**
     * 零工作业分类名称
     */
    private String oddWorkTypeName;
}

