package com.yy.ppm.business.bean.dto.contract;

import lombok.Getter;
import lombok.Setter;

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
    private String tradeType;

    /**
     * 进出口，进口、出口、进/出口
     */
    private String impExp;

    /**
     * 货物
     */
    private List<TBusTrateItemCargoDTO> cargos;

    /**
     * 是否阶梯费率 0否/1是
     */
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
}
