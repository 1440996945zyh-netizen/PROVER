package com.yy.ppm.master.bean.dto.tufFee;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 拖轮费用标准(MTugFee)DTO
 *
 * @author linqi
 * @since 2023-09-27 10:35:29
 */
@Setter
@Getter
public class MTugFeeDTO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 费目编码
     */
    private String itemCd;

    /**
     * 费目
     */
    private String itemNm;

    /**
     * 内外贸，内贸/外贸
     */
    private String tradeType;

    /**
     * 船长度
     */
    private Integer shipLength;

    /**
     * 船型编码，字典SHIP_TYPE
     */
    private String shipTypeCode;

    /**
     * 船型Label
     */
    private String shipTypeLabel;

    /**
     * 单价
     */
    private BigDecimal rate;

    /**
     * 税率
     */
    private BigDecimal taxRate;
}

