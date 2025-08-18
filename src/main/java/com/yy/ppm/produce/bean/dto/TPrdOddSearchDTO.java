package com.yy.ppm.produce.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Auther wangxd
 * @Description
 * @Date 2023-12-12 10:08
 */
@Setter
@Getter
public class TPrdOddSearchDTO {
    /**
     * 状态
     */
    private String status;
    /**
     * 部门列表
     */
    private List<String> deptNos;
    /**
     * 部门列表
     */
    private List<Long> deptIds;
    /**
     * 零工类型(默认，根据登录用户)
     */
    private String defaultOddType;
    /**
     * 零工类型(前端查询条件)
     */
    private String oddType;

    /**
     * 申请开始时间(起)
     */
    private String searchStartTime;

    /**
     * 申请开始时间(止)
     */
    private String searchEndTime;

    /**
     * 页面编号（1 申请页面；2 审核页面；3 汇总审核页面）
     */
    private String pageType;

    private String deptNo;

    private String oddPlanNo;

    private Long createFromDeptId;

    /**
     * 申请部门列表
     */
    private List<Long> createByDeptIds;

    private String isReject;

    private String workContent;
}
