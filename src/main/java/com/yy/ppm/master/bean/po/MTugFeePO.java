package com.yy.ppm.master.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 拖轮费用标准(MTugFee)PO
 *
 * @author linqi
 * @since 2023-09-27 10:35:29
 */
@Setter
@Getter
public class MTugFeePO extends BasePO {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空")
    private Long id;

    /**
     * 费目编码
     */
    @NotNull(message = "费目编码不能为空")
    private String itemCd;

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
     * 单价
     */
    @NotNull(message = "单价不能为空")
    private BigDecimal rate;

    /**
     * 税率
     */
    @NotNull(message = "税率不能为空")
    private BigDecimal taxRate;
}

