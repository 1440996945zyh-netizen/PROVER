package com.yy.ppm.business.bean.dto.cargoInfo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class ExportDTO {

    @ExcelIgnore
    private Long id;
    @ExcelIgnore
    private String type;
    @ExcelProperty(value = "票货号")
    @ColumnWidth(value = 19)
    private String cargoInfoNo;
    @ExcelProperty(value = "船名航次")
    @ColumnWidth(value = 20)
    private String shipNameVoyage;
    @ExcelProperty(value = "合同号")
    @ColumnWidth(value = 17)
    private String contractCode;
    @ExcelProperty(value = "货主名称")
    @ColumnWidth(value = 23)
    private String cargoOwnerName;
    @ExcelProperty(value = "货物名称")
    @ColumnWidth(value = 15)
    private String cargoName;
    @ExcelProperty(value = "贸别")
    @ColumnWidth(value = 8)
    private String tradeType;
    @ExcelProperty(value = "包装名称")
    @ColumnWidth(value = 14)
    private String packingName;
    @ExcelProperty(value = "件数")
    @ColumnWidth(value = 8)
    private String quantity;
    @ExcelProperty(value = "重量")
    @ColumnWidth(value = 8)
    private BigDecimal ton;
    @ExcelProperty(value = "交接清单量")
    @ColumnWidth(value = 15)
    private BigDecimal handoverlistTon;
    @ExcelProperty(value = "已下发计划量")
    @ColumnWidth(value = 17)
    private BigDecimal trustCargoTon;
    @ExcelProperty(value = "已完成量")
    @ColumnWidth(value = 15)
    private BigDecimal weightGoods;
    @ExcelProperty(value = "剩余计划量")
    @ColumnWidth(value = 14)
    private BigDecimal balanceTon;
    @ExcelProperty(value = "剩余港存量")
    @ColumnWidth(value = 14)
    private BigDecimal balancePortStorageTon;
    @ExcelProperty(value = "货权量")
    @ColumnWidth(value = 11)
    private String rightsQuantity;
    @ExcelProperty(value = "剩余货权量")
    @ColumnWidth(value = 14)
    private String surplusRightsQuantity;
    @ExcelProperty(value = "预缴状态")
    @ColumnWidth(value = 11)
    private String prePayStatus;
    @ExcelProperty(value = "预缴编号")
    @ColumnWidth(value = 12)
    private String prePayNo;
    @ExcelIgnore
    private String isClear;
    @ExcelProperty(value = "是否完货")
    @ColumnWidth(value = 12)
    private String isClearLabel;
    @ExcelProperty(value = "作业公司")
    @ColumnWidth(value = 30)
    private String companyName;
    @ExcelProperty(value = "放货人")
    @ColumnWidth(value = 21)
    private String releaseByNames;
    @ExcelProperty(value = "第一次放货时间")
    @ColumnWidth(value = 19)
    private String releaseTimes;
}
