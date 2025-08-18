package com.yy.ppm.produce.bean.dto.portStorage;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-24 15:21
 */
@Setter
@Getter
public class TPrdPortStorageGbCargoOwnerDTO {

    /**
     * 货主ID
     */
    @ExcelIgnore
    private Long cargoOwnerId;

    /**
     * 货主
     */
    @ExcelProperty(value = "货主", index = 1)
    private String cargoOwnerName;

    /**
     * 货物编码
     */
    @ExcelIgnore
    private String cargoCode;

    /**
     * 货名
     */
    @ExcelProperty(value = "货名", index = 2)
    private String cargoName;

    /**
     * 作业公司名称
     */
    @ExcelProperty(value = "作业公司", index = 0)
    private String companyName;

    /**
     * 件数
     */
    @ExcelProperty(value = "件数", index = 3)
    private Integer quantity;

    /**
     * 吨数（数量）
     */
    @ExcelProperty(value = "重量", index = 4)
    private BigDecimal ton;

    /**
     * 票货信息label
     */
    @ExcelIgnore
    private String cargoInfoLabel;
}
