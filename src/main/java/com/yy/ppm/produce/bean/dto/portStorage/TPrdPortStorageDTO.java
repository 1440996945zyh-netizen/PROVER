package com.yy.ppm.produce.bean.dto.portStorage;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-24 15:21
 */
@Setter
@Getter
public class TPrdPortStorageDTO implements Serializable {

    @ExcelIgnore
    private Long id;

    /**
     * 作业日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ExcelIgnore
    private Date workDate;

    /**
     * 作业班次
     */
    @ExcelIgnore
    private String classCode;
    @ExcelIgnore
    private String className;
    @ExcelIgnore
    private String processDetailName;

    /**
     * 作业公司
     */
    @ExcelIgnore
    private Long companyId;

    /**
     * 票货ID
     */
    @ExcelIgnore
    private Long cargoInfoId;

    /**
     * 场地ID
     */
    @ExcelIgnore
    private Long storehouseId;

    /**
     * 场地名称
     */
    @ExcelProperty(value = "场地", index = 8)
    private String storehouseName;

    /**
     * 区域ID
     */
    @ExcelIgnore
    private Long regionId;

    /**
     * 区域名称
     */
    @ExcelProperty(value = "区域", index = 9)
    private String regionName;

    /**
     * 堆ID
     */
    @ExcelIgnore
    private Long massId;

    /**
     * 堆名称
     */
    @ExcelProperty(value = "垛位", index = 10)
    private String massName;

    /**
     * 作业公司名称
     */
    @ExcelProperty(value = "作业公司", index = 2)
    private String companyName;

    /**
     * 船名航次
     */
    @ExcelProperty(value = "船名航次", index = 3)
    private String shipNameVoyage;

    /**
     * 内外贸
     */
    @ExcelProperty(value = "贸别", index = 4)
    private String tradeType;

    /**
     * 货主
     */
    @ExcelProperty(value = "货主", index = 5)
    private String cargoOwnerName;

    /**
     * 货代
     */
    @ExcelIgnore
    private String cargoAgentName;

    /**
     * 货名
     */
    @ExcelProperty(value = "货名", index = 6)
    private String cargoName;

    /**
     * 第一次进货日期
     */
    @ExcelProperty(value = "创建时间", index = 7)
    private String inoutDate;

    /**
     * 包装
     */
    @ExcelProperty(value = "包装", index = 11)
    private String packingName;

    /**
     * 件数
     */
    @ExcelProperty(value = "件数", index = 12)
    private Integer quantity;

    /**
     * 吨数（数量）
     */
    @ExcelProperty(value = "重量", index = 13)
    private BigDecimal ton;

    /**
     * 票货信息label
     */
    @ExcelIgnore
    private String cargoInfoLabel;

    /**
     * 票货号No
     */
    @ExcelProperty(value = "票货号", index = 1)
    private String cargoInfoNo;

    /**
     * 指令编号
     */
    @ExcelProperty(value = "指令编号", index = 0)
    private String trustNo;

    /**
     * 船名航次
     */
    @ExcelIgnore
    private Long shipvoyageItemId;

    /**
     * 船名
     */
    @ExcelIgnore
    private String shipName;

    /**
     * 航次
     */
    @ExcelIgnore
    private String voyage;

    /**
     * 是否清场,INOUT_STORAGE_CODE
     */
    @ExcelIgnore
    private String inoutStorageCode;

    /**
     * 是否清场
     */
    @ExcelProperty(value = "是否清场", index = 14)
    private String inoutStorageName;
}
