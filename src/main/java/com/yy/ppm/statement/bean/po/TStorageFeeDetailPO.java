package com.yy.ppm.statement.bean.po;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 历史堆存费结算明细信息
 * */
@Data
public class TStorageFeeDetailPO implements Serializable {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 历史结算主表id
     */
    private Long historyId;

    /**
     * 堆存日期
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date storageDate;

    /**
     * 堆存天数
     */
    private Integer days;

    /**
     * 场存重量
     */
    private BigDecimal fieldStockWeight;

    /**
     * 进场量
     */
    private BigDecimal entryWeight;

    /**
     * 出厂量
     */
    private BigDecimal appearanceWeight;

    /**
     * 免堆存量
     */
    private BigDecimal feeStorage;

    /**
     * 结算金额
     */
    private BigDecimal amount;

    /**
     * 费率
     */
    private BigDecimal rate;

    /**
     * 结算堆存量
     */
    private BigDecimal settlementVolume;

    private static final long serialVersionUID = 1L;
}

