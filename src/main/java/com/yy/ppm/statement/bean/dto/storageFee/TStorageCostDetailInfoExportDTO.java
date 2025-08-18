package com.yy.ppm.statement.bean.dto.storageFee;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.excel.export.bean.SheetMapping;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class TStorageCostDetailInfoExportDTO extends SheetMapping {


    /**
     * 本日应计费量（减免前）
     */
    @ExcelIgnore()
    private BigDecimal originalBillableTon;
    /**
     * 本日结算金额（减免前）
     */
    @NotNull(message = "本日结算金额（减免前）不能为空")
    private BigDecimal originalAmount;
    /**
     * 日期
     */
    @NotNull(message = "日期不能为空")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date date;

    /**
     * 本日堆存量
     */
    @ExcelProperty("堆存量")
    private BigDecimal ton;

    /**
     * 本日应计费量
     */
    @NotNull(message = "本日应计费量不能为空")
    @ExcelProperty("计费量")
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


}
