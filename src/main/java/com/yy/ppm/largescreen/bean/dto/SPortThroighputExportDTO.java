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
public class SPortThroighputExportDTO implements Serializable {

    @ExcelProperty(value = "港区代码", index = 0)
    @ExcelIgnore
    private String portCode;

    @ExcelProperty(value = "港区", index = 1)
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
    /**
     * 时间类型（1：年，2：月，3：日）（3个抽屉）
     */
    @ExcelProperty(value = "时间代码", index = 2)
    @ExcelIgnore
    private String dateType;

    @ExcelProperty(value = "时间类型", index = 3)
    private String dateTypeLabel;
    public void setDateType(String dateType) {
        this.dateType = dateType;
        if("1".equals(dateType)){
            this.dateTypeLabel = "年";
        } else if("2".equals(dateType)){
            this.dateTypeLabel = "月";
        } else if("3".equals(dateType)){
            this.dateTypeLabel = "日";
        }
    }

    @ExcelProperty(value = "时间", index = 4)
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateTime;

    @ExcelProperty(value = "类型代码", index = 5)
    @ExcelIgnore
    private String cargoType;

    @ExcelProperty(value = "货物类型", index = 6)
    private String cargoTypeLabel;

    public void setCargoType(String cargoType) {
        this.cargoType = cargoType;
        if("1".equals(cargoType)){
            this.cargoTypeLabel = "件货";
        } else if("2".equals(cargoType)){
            this.cargoTypeLabel = "散货";
        }
    }

    @ExcelProperty(value = "吨数", index = 7)
    private String ton;

    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间", index = 8)
    private Date createTime;


}
