package com.yy.ppm.business.bean.dto.trate;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Auther linqi
 * @Description 阶梯费率条目表
 * @Date 2023-11-08 11:40
 */
@Setter
@Getter
public class TBusTrateItemDTO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 阶梯费率ID
     */
    private Long trateId;

    /**
     * 贸别，内贸、外贸、内/外贸
     */
    @NotBlank(message = "贸别不能为空")
    private String tradeType;

    /**
     * 进出口，进口、出口、进/出口
     */
    @NotBlank(message = "进出口不能为空")
    private String impExp;

    /**
     * 货物
     */
    @NotEmpty(message = "货物不能为空")
    private List<TBusTrateItemCargoDTO> cargos;

    /**
     * 是否阶梯费率 0否/1是
     */
    @NotBlank(message = "是否阶梯费率不能为空")
    private String isTieredRate;

    /**
     * 优惠费率值，非阶梯费率时非空
     */
    private Integer preferentialRate;

    /**
     * 原始累积量
     */
    private BigDecimal originAccNumber;

    /**
     * 明细，是阶梯费率时非空
     */
    private List<TBusTrateItemDetailDTO> details;

    /**
     * 计费量
     */
    private BigDecimal statementNumber;

    /**
     * 累积量
     */
    private BigDecimal accNumber;
}
