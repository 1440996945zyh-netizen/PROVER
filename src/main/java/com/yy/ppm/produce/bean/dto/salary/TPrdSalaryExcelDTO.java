package com.yy.ppm.produce.bean.dto.salary;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther wangxd
 * @Description 计件工资导出实体
 * @Date 2024-01-04 13:43
 */
@Setter
@Getter
public class TPrdSalaryExcelDTO {

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

    @ExcelProperty(value = "单价", index = 10)
    private BigDecimal price;

    @ExcelProperty(value = "件数", index = 11)
    private Integer quantity;

    @ExcelProperty(value = "吨数/时长", index = 12)
    private BigDecimal ton;

    @ExcelProperty(value = "金额", index = 13)
    private BigDecimal amount;

    @ExcelProperty(value = "备注", index = 14)
    private String remark;

    @ExcelProperty(value = "状态", index = 15)
    private String salaryStatusName;

    @ExcelProperty(value = "零工内容", index = 16)
    private String workContent;

    @ExcelProperty(value = "零工编号", index = 17)
    private String oddPlanNo;

    @ExcelProperty(value = "主过程", index = 18)
    private String processName;
}
