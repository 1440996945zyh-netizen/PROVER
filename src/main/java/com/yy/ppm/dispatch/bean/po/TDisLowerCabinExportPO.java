package com.yy.ppm.dispatch.bean.po;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class TDisLowerCabinExportPO implements Serializable {

    @ExcelProperty(value = "门机编号", index = 0)
    private String equipmentNo;

    @ExcelProperty(value = "舱口", index = 1)
    private String cabinNo;

    @ExcelProperty(value = "开始时间", index = 2)
    private String startTime;

    @ExcelProperty(value = "结束时间", index = 3)
    private String endTime;

    @ExcelProperty(value = "时长", index = 4)
    private String cnsc;

    @ExcelProperty(value = "工作量", index = 5)
    private String workload;

    @ExcelProperty(value = "备注", index = 6)
    private String remark;


}
