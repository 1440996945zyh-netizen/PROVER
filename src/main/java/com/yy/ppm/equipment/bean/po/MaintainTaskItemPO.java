package com.yy.ppm.equipment.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.dto.SysFileDTO;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class MaintainTaskItemPO extends BasePO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 点检任务ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipTaskId;
    /**
     * 点检标准ID
     */
    private Long standardId;
    /**
     * 点检计划ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipPlanId;
    /**
     * 点检计划子表ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipPlanItemId;
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
     * 设备名称
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipId;
    /**
     * 设备名称
     */
    private String equipName;
    /**
     * 设备名称
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long inspectorId;
    /**
     * 设备部件
     */
    private String inspectorName;
    /**
     * 点检内容
     */
    private String content;
    /**
     * 点检标准
     */
    private String standard;
    /**
     * 是否异常 0否 1是
     */
    private String isAbnormal;
    /**
     * 备注
     */
    private String remark;
    private String status;
    private String deptName;
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
    private String equipType;
    private String fileIds;
    /**
     * 是否报修 0否 1是
     */
    private Integer isRepair;
    /** 附件 */
    private List<SysFileDTO> mattachmentInfoList;

    /**
     * 计划类型
     */
    private String planType;
}
