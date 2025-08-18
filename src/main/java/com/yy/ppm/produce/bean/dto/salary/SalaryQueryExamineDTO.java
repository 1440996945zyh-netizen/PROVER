package com.yy.ppm.produce.bean.dto.salary;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-04 17:08
 */
@Setter
@Getter
public class SalaryQueryExamineDTO {

    /**
     * 0:审核 1:取消审核
     */
    @NotNull(message = "审核标记不能为空")
    private String flag;

    /**
     * Y:是 N:否
     */
    @NotNull(message = "审核标记不能为空")
    private String isHr;

    /**
     * 审核ID
     */
    @NotNull(message = "审核ID不能为空")
    private List<Long> ids;

    /**
     * 月份
     */
    @DateTimeFormat(pattern = "yyyy-MM")
    private Date month;
    private String startDay;
    private String endDay;
    private String auditMonth;

    private Long deptId;
    private Long companyId;
    private String salaryTypeCode;

    private String salaryStatusCode;

    private String salaryStatusName;

    private String classCode;

    private Long examineByHr;
    private String examineByNameHr;
    private Date examineTimeHr;

    private String salaryStatusCodeWhere;

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
     * 是否零工
     */
    private String isOdd;

}
