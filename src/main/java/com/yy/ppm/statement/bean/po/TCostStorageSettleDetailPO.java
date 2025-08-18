package com.yy.ppm.statement.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther linqi
 * @Description 堆存费结算明细表
 * @Date 2023-11-24 9:57
 */
@Setter
@Getter
public class TCostStorageSettleDetailPO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 结算主表ID
     */
    private Long storageSettleId;

    /**
     * 日期
     */
    @NotNull(message = "日期不能为空")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date date;

    /**
     * 本日堆存量
     */
    @NotNull(message = "本日堆存量不能为空")
    private BigDecimal ton;

    /**
     * 本日应计费量
     */
    @NotNull(message = "本日应计费量不能为空")
    private BigDecimal billableTon;

    /**
     * 本日进场量
     */
    @NotNull(message = "本日进场量不能为空")
    private BigDecimal inTon;

    /**
     * 本日出场量
     */
    @NotNull(message = "本日出场量不能为空")
    private BigDecimal outTon;

    /**
     * 本日结算金额
     */
    @NotNull(message = "本日结算金额不能为空")
    private BigDecimal amount;

    /**
     * 本日结算税额
     */
    @NotNull(message = "本日结算税额不能为空")
    private BigDecimal taxAmount;
}
