package com.yy.ppm.produce.bean.dto.salary;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class TPrdSalaryGroupByProcessDTO {

    @ExcelProperty(value = "操作过程", index = 0)
    private String processNm;

    @ExcelProperty(value = "子过程", index = 1)
    private String processDetailName;

    @ExcelProperty(value = "吨数", index = 2)
    private BigDecimal ton;
}
