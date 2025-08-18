package com.yy.ppm.business.bean.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CargoTypeDTO {
    /**
     * 货类code
     */
    private String cargoTypeCode;
    /**
     * 货类名字
     */
    private String cargoTypeName;
    /**
     * 货类数量
     */
    private BigDecimal allCount;
}
