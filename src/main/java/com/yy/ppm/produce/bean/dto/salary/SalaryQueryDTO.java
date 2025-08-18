package com.yy.ppm.produce.bean.dto.salary;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-04 17:08
 */
@Setter
@Getter
public class SalaryQueryDTO {

    /**
     * 作业公司ID
     */
    private String companyId;

    /**
     * 部门ID
     */
    private String deptId;

    /**
     * 月份
     */
    private String month;

    /**
     * 开始日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    /**
     * 结束日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    /**
     * 作业日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date workDate;

    /**
     * 班次编码
     */
    private String classCode;

    /**
     * 计件工资项目编码 字典PIECE_PROJECT
     */
    private String pieceProjectCode;

    /**
     * 子过程编码
     */
    private String processDetailCode;

    /**
     * 审核状态 字典SALARY_STATUS
     */
    private String salaryStatusCode;
    private String startDay;
    private String endDay;
    /**
     * 月份
     */
    private String auditMonth;

    private String salaryTypeCode;
    private String isOdd;

    private String userByName;
}
