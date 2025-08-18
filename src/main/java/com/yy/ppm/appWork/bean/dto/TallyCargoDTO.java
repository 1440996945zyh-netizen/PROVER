package com.yy.ppm.appWork.bean.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 理货量
 */
@Getter
@Setter
@ToString
public class TallyCargoDTO implements Serializable {

    private static final long serialVersionUID = -7734686200034099011L;

    private Long cargoInfoId;

    private Integer quantity;

    private BigDecimal ton;

    private Integer zqQuantity;

    private BigDecimal zqTon;

    private Integer zyQuantity;

    private BigDecimal zyTon;
}

