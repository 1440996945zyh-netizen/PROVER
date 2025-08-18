package com.yy.ppm.master.bean.dto;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.yy.common.excel.export.bean.SheetMapping;
import com.yy.common.page.PageParameter;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
@ToString
public class WaifuProcessPriceExcelDTO{

    @ExcelIgnore
    private Long id;
    @ExcelIgnore
    private String waifuPackageCode;
    @ExcelProperty(value = "外付包装类型", index = 1)
    @ColumnWidth(value = 20)
    private String waifuPackageName;
    @ExcelIgnore
    private String processCode;
    @ExcelProperty(value = "作业过程", index = 2)
    @ColumnWidth(value = 20)
    private String processName;
    @ExcelIgnore
    private String processDetailCode;
    @ExcelProperty(value = "二级作业过程", index = 4)
    @ColumnWidth(value = 20)
    private String processDetailName;
    @ExcelIgnore
    private long deptId;
    @ExcelProperty(value = "外包单位名称", index = 0)
    @ColumnWidth(value = 18)
    private String deptName;
    @ExcelProperty(value = "费率", index = 7)
    private BigDecimal rate;
    @ExcelIgnore
    private String positionCode;
    @ExcelProperty(value = "位置", index = 3)
    @ColumnWidth(value = 12)
    private String positionName;
    @ExcelIgnore
    private String allotType;
    //2,机械  3，人员
    @ExcelProperty(value = "分配类型", index = 5)
    @ColumnWidth(value = 18)
    private String allotTypeName;
    @ExcelIgnore
    private String machineTypeCode;
    @ExcelProperty(value = "机械类型", index = 6)
    @ColumnWidth(value = 16)
    private String machineTypeName;
    @ExcelProperty(value = "备注", index = 8)
    @ColumnWidth(value = 20)
    private String remark;

    public void setAllotType(String allotType) {
        this.allotType = allotType;
        if("2".equals(allotType)){
            this.allotTypeName = "机械分配";
        }else if("3".equals(allotType)){
            this.allotTypeName = "人员分配";
        }
    }
}
