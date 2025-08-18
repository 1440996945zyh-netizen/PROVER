package com.yy.common.excel.export.bean;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

/**
 * @Author linqi
 * @Description 模板sheet数据映射
 * @Date 2023-05-18 13:55
 */
@Setter
@Getter
public class SheetMapping {

    @NotBlank(message = "sheet名字不能为空")
    private String sheetName;
}
