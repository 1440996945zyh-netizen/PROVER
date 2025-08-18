package com.yy.ppm.produce.bean.dto.portStorage;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-24 15:21
 */
@Setter
@Getter
public class TPrdPortStorageGbCargoInfoDTO {

    /**
     * 票货ID
     */
    @ExcelIgnore
    private Long cargoInfoId;

    /**
     * 作业公司名称
     */
    @ExcelProperty(value = "作业公司", index = 2)
    private String companyName;

    /**
     * 第一次进货日期
     */
    @ExcelProperty(value = "入场日期", index = 4)
    private String inoutDate;

    /**
     * 船名航次
     */
    @ExcelProperty(value = "船名航次", index = 3)
    private String shipNameVoyage;

    /**
     * 内外贸
     */
    @ExcelProperty(value = "贸别", index = 5)
    private String tradeType;

    /**
     * 货主
     */
    @ExcelProperty(value = "货主", index = 6)
    private String cargoOwnerName;

    /**
     * 货代
     */
    @ExcelIgnore
    private String cargoAgentName;

    /**
     * 货名
     */
    @ExcelProperty(value = "货名", index = 7)
    private String cargoName;

    /**
     * 包装
     */
    @ExcelProperty(value = "包装", index = 8)
    private String packingName;

    /**
     * 件数
     */
    @ExcelProperty(value = "件数", index = 9)
    private Integer quantity;

    /**
     * 吨数（数量）
     */
    @ExcelProperty(value = "重量", index = 10)
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
     * trustNo
     */
    @ExcelProperty(value = "指令编号", index = 0)
    private String trustNo;

    private List<TPrdPortStorageDTO> table;
}
