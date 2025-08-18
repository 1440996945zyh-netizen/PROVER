package com.yy.ppm.business.bean.dto.trate;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "起始量不能为空")
    private BigDecimal beginTon;

    /**
     * 截止量，<=
     */
    @NotNull(message = "截止量不能为空")
    private BigDecimal endTon;

    /**
     * 费率值
     */
    @NotNull(message = "费率值不能为空")
    private BigDecimal rate;
}
