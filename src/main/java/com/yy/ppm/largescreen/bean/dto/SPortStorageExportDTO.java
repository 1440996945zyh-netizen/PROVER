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
public class SPortStorageExportDTO implements Serializable {

    @ExcelIgnore
    private String portCode;

    @ExcelProperty(value = "港区", index = 0)
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

    @ExcelProperty(value = "货名", index = 1)
    private String cargoName;

    @ExcelProperty(value = "计划量", index = 2)
    private String ton;

    @ExcelIgnore
    private String cargoType;

    public void setCargoType(String cargoType) {
        this.cargoType = cargoType;
        if("1".equals(cargoType)){
            this.cargoTypeLabel = "件货";
        } else if("2".equals(cargoType)){
            this.cargoTypeLabel = "散货";
        }
    }
    @ExcelProperty(value = "货物类型", index = 3)
    private String cargoTypeLabel;

    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间", index = 4)
    private Date createTime;



}
