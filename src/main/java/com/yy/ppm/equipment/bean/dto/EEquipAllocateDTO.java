package com.yy.ppm.equipment.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.equipment.bean.po.EEquipAllocatePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 设备调拨DTO
 * @author system
 */
@Getter
@Setter
@ToString
public class EEquipAllocateDTO extends EEquipAllocatePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 调入单位名称
     */
    private String toCompanyName;

    /**
     * 调入部门名称
     */
    private String toOrgName;

    /**
     * 申请人姓名
     */
    private String applyUserName;

    /**
     * 审批状态名称
     */
    private String statusName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    /**
     * 修改时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifiedTime;

    /**
     * 设备列表
     */
    private List<AllocateEquipDTO> equipList;
    /**
     * 流程状态
     */
    private String processStatus;
    /**
     * 流程状态标签
     */
    private String processStatusLabel;
    /**
     * 流程实例ID
     */
    private String procInstId;


}
