package com.yy.ppm.business.bean.dto.contract;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @Auther linqi
 * @Description 阶梯费率条目明细表
 * @Date 2023-11-08 11:41
 */
@Setter
@Getter
public class TBusTrateItemDetailDTO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 阶梯费率条目ID
     */
    private Long trateItemId;

    /**
     * 起始量，>
     */
    private BigDecimal beginTon;

    /**
     * 截止量，<=
     */
    private BigDecimal endTon;

    /**
     * 费率值
     */
    private BigDecimal rate;
}
