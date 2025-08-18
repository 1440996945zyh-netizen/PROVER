package com.yy.ppm.produce.bean.dto.salary;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-11-30 19:43
 */
@Setter
@Getter
public class TPrdSalaryDTO {

    @ExcelProperty(value = "作业公司", index = 0)
    private String companyName;

    @ExcelProperty(value = "部门", index = 1)
    private String deptName;

    @DateTimeFormat("yyyy-MM-dd")
    @ExcelProperty(value = "作业日期", index = 2)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date workDate;

    @ExcelProperty(value = "班次", index = 3)
    private String className;

    @ExcelProperty(value = "船名航次", index = 4)
    private String shipName;

    @ExcelProperty(value = "货名", index = 5)
    private String cargoName;

    @ExcelProperty(value = "包装", index = 6)
    private String salaryTypeName;

    @ExcelProperty(value = "子过程", index = 7)
    private String processDetailName;

    @ExcelProperty(value = "人员", index = 8)
    private String userByName;

    @ExcelProperty(value = "分配系数", index = 9)
    private Integer coefficient;

    @ExcelProperty(value = "件数", index = 10)
    private Integer quantity;

    @ExcelProperty(value = "吨数/时长", index = 11)
    private BigDecimal ton;

    @ExcelProperty(value = "状态", index = 12)
    private String salaryStatusName;

    @ExcelProperty(value = "备注", index = 13)
    private String remark;

    @ExcelProperty(value = "零工内容", index = 14)
    private String workContent;

    @ExcelProperty(value = "零工编号", index = 15)
    private String oddPlanNo;

    @ExcelProperty(value = "主过程", index = 16)
    private String processName;

}
