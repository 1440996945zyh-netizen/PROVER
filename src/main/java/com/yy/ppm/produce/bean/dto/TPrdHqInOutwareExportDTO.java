package com.yy.ppm.produce.bean.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class TPrdHqInOutwareExportDTO implements Serializable {

    @ExcelProperty(value = "作业时间", index = 0)
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ExcelProperty(value = "货主", index = 1)
    private String cargoOwnerName;

    @ExcelProperty(value = "计划号", index = 2)
    private String planNo;

    @ExcelProperty(value = "票货编号", index = 3)
    private String cargoInfoNo;

    @ExcelProperty(value = "货物名称", index = 4)
    private String cargoName;

    @ExcelProperty(value = "海清货物名称", index = 5)
    private String hqCargoName;

    @ExcelProperty(value = "喷码编号", index = 6)
    private String pqNo;

    @ExcelProperty(value = "件数", index =7)
    private String quantity;

    @ExcelProperty(value = "吨数", index =8)
    private String ton;

    @ExcelProperty(value = "长(m)", index = 9)
    private String hqLength;

    @ExcelProperty(value = "宽(m)", index = 10)
    private String width;

    @ExcelProperty(value = "高(m)", index = 11)
    private String height;

    @ExcelProperty(value = "立方量(m^3)", index = 12)
    private String volume;

    @ExcelProperty(value = "区域", index = 13)
    private String stackPositionName;

    @ExcelProperty(value = "垛位", index = 14)
    private String yardName;

    @ExcelProperty(value = "车牌号", index = 15)
    private String transportEquipmentNo;

    @ExcelProperty(value = "皮重", index = 16)
    private String weightSelf;

    @ExcelProperty(value = "毛重", index = 17)
    private String weightAll;

    @ExcelProperty(value = "净重", index = 18)
    private String weightGoods;

    @ExcelProperty(value = "进港日期", index = 19)
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date weighInDt;

    @ExcelProperty(value = "出港日期", index = 20)
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date weighOutDt;

    @ExcelProperty(value = "理货员", index = 21)
    private String createByName;

    @ExcelProperty(value = "出入库状态", index = 22)
    private String status;
    @ExcelProperty(value = "入库时间", index = 23)
    private String inPortTime;
    @ExcelProperty(value = "入库人", index = 24)
    private String inPortName;
    @ExcelProperty(value = "出库时间", index = 25)
    private String outPortTime;
    @ExcelProperty(value = "出库人", index = 26)
    private String outPortName;
    @ExcelProperty(value = "卸船船名航次", index = 27)
    private String inShipVoyage;
    @ExcelProperty(value = "装船船名航次", index = 28)
    private String outShipVoyage;

    @ExcelIgnore
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date beginDate;
    @ExcelIgnore
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
}
