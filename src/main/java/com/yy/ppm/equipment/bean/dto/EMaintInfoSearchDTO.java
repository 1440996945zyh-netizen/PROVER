package com.yy.ppm.equipment.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;

/**
 * 设备维修派工信息查询DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaintInfoSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    private Long equipId;

    /**
     * 设备名称
     */
    private String equipName;

    /**
     * 设备编码
     */
    private String equipCode;

    /**
     * 工单号
     */
    private String workOrderNo;

    /**
     * 维修类型代码
     */
    private String maintTypeCode;

    /**
     * 派工类型代码
     */
    private String dispatchTypeCode;

    /**
     * 紧急程度
     */
    private String emergencyLevel;

    /**
     * 是否停机（0-否，1-是）
     */
    private Integer isStopped;

    /**
     * 状态（0-提报，1-已派工，2-维修中，4-维修完成，5-验收通过，6-验收不通过，7-作废）
     */
    private Integer status;

    /**
     * 创建人ID（用于权限过滤）
     */
    private Long createBy;

    /**
     * 维修负责人ID（用于权限过滤）
     */
    private Long maintLeaderId;
}

