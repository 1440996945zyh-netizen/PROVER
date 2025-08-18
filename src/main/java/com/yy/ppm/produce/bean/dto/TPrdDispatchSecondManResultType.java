package com.yy.ppm.produce.bean.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 二次派工的劳务关系中专用
 */
@Getter
@Setter
@ToString
public class TPrdDispatchSecondManResultType {

    /**
     * 公司id
     */
    private Long companyId;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 班次id
     */
    private Long deptId;
    /**
     * 班次名称
     */
    private String deptName;

    /***
     * 人数
     */
    private Long numberCount;

    /**
     * 工班计划ID
     */
    private Long workPlanId;

    private String workPositionCode;
    private String workPositionName;
    
    /**
     * 子作业过程CODE
     */
    private String subProcessCode;

    /**
     * 子作业过程名称
     */
    private String subProcessName;
    /**
     * 装卸队派工用来存部门
     */
    private Long deptParentId;
    /**
     * 装卸队派工用来存部门
     */
    private String deptParentName;

}
