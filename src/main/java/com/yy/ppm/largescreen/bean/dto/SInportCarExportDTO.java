package com.yy.ppm.largescreen.bean.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class SInportCarExportDTO implements Serializable {
    @ExcelProperty(value = "车牌号", index = 0)
    private String carNum;

    @ExcelProperty(value = "船名", index = 1)
    private String shipName;


    @ExcelProperty(value = "港区", index = 2)
    private String portLabel;
    public void setPortCode(String portCode) {
        this.portCode = portCode;
        if("10".equals(portCode)){
            this.portLabel = "潍坊港";
        } else if("20".equals(portCode)){
            this.portLabel = "寿光港";
        } else if("30".equals(portCode)){
            this.portLabel = "东营港";
        } else if("40".equals(portCode)){
            this.portLabel = "滨州港";
        }
    }

    @ExcelProperty(value = "货名", index = 3)
    private String cargoName;

    @ExcelProperty(value = "计划量", index = 4)
    private String planTon;

    @ExcelProperty(value = "货主", index = 5)
    private String cargoOwner;

    @ExcelProperty(value = "进港时间", index = 6)
    private String inPortDate;

    @ExcelProperty(value = "在港时长", index = 7)
    private String inPortTime;

    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间", index = 8)
    private Date createTime;

    @ExcelProperty(value = "港区代码", index = 9)
    @ExcelIgnore
    private String portCode;

}

