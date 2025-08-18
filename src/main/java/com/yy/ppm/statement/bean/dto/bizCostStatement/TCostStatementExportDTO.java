package com.yy.ppm.statement.bean.dto.bizCostStatement;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-14 14:36
 */
@Setter
@Getter
public class TCostStatementExportDTO {

    private Long id;
    private String InTime;
    private String berthName;
    private Long shipvoyageItemId;
    /**
     * 结算单编号
     */
    @ExcelProperty(value = "结算单编号")
    @ColumnWidth(18)
    private String statementNo;
    /**
     * 作业公司NAME
     */
    @ExcelProperty(value = "作业公司")
    @ColumnWidth(30)
    private String companyName;


    /**
     * 客户名称
     */
    @ExcelProperty(value = "结算单位")
    @ColumnWidth(35)
    private String customerName;
    @ExcelProperty(value = "应收港务费结算单位")
    @ColumnWidth(35)
    private String customerNameYs;

    /**
     * 结算单类型（10.船舶货方结算单、20.陆集陆疏货方结算单 ）30.船方计费 40.杂项计费
     */
    @ExcelProperty(value = "结算单类型")
    @ColumnWidth(18)
    private String type;
    @ExcelProperty(value = "费用名称")
    @ColumnWidth(18)
    private String rateItemName;
    @ColumnWidth(20)
    @ExcelProperty(value = "船名航次")
    private String shipNameVoyage;
    @ColumnWidth(12)
    @ExcelProperty(value = "票货号")
    private String cargoInfoNo;
    /**
     * 货名
     */
    @ExcelProperty(value = "货名")
    @ColumnWidth(18)
    private String cargoName;

    /**
     * 贸别，内贸、外贸
     */
    @ExcelProperty(value = "贸别")
    @ColumnWidth(10)
    private String tradeType;
    @ExcelProperty(value = "进出口")
    @ColumnWidth(10)
    private String impExp;

    /**
     * 离泊时间
     */
    @ExcelProperty(value = "离泊时间",format="yyyy-MM-dd HH:mm")
    @ColumnWidth(20)
    private String leaveBerthTime;

    @ExcelProperty(value = "作业任务单号")
    @ColumnWidth(20)
    private String businessNo;
    @ExcelProperty(value = "作业内容")
    @ColumnWidth(20)
    private String workContent;
    @ExcelProperty(value = "委托单位")
    @ColumnWidth(20)
    private String wtdw;
    @ExcelProperty(value = "作业地点")
    @ColumnWidth(20)
    private String workLocation;
    /**
     * 结算日期
     */
    @ExcelProperty(value = "结算日期")
    @ColumnWidth(14)
    private String settlementDate;
    @ExcelProperty(value = "支付方式")
    @ColumnWidth(14)
    private String payTypeName;



    /**
     * 作业过程名称
     */
    @ExcelProperty(value = "作业过程")
    @ColumnWidth(16)
    private String processName;
    /**
     * 服务内容名称
     */
    @ExcelProperty(value = "服务内容")
    @ColumnWidth(14)
    private String serviceContentName;

    /**
     * 计费数量
     */
    @ExcelProperty(value = "数量1")
    @ColumnWidth(15)
    private BigDecimal number;
    /**
     * 计费数量2（船舶净吨）
     */
    @ExcelProperty(value = "数量2")
    @ColumnWidth(15)
    private BigDecimal number2;


    /**
     * 费率值
     */
    @ExcelProperty(value = "费率")
    @ColumnWidth(15)
    private BigDecimal rate;

    /**
     * 计费单位名称
     */
    @ExcelProperty(value = "计费单位")
    @ColumnWidth(15)
    private String unitName;

    @ExcelProperty(value = "计费金额")
    @ColumnWidth(15)
    private BigDecimal amountjf;
    @ExcelProperty(value = "折扣金额")
    @ColumnWidth(15)
    private BigDecimal amountzk;
    @ExcelProperty(value = "结算单金额")
    @ColumnWidth(15)
    private BigDecimal amount;




    /**
     * 税率值
     */
    @ExcelProperty(value = "税率")
    @ColumnWidth(15)
    private BigDecimal tax;

    /**
     * 税额
     */
    @ExcelProperty(value = "税额")
    @ColumnWidth(15)
    private BigDecimal taxAmount;
    @ExcelProperty(value = "开票金额")
    @ColumnWidth(15)
    private BigDecimal invoiceAmount;
    @ExcelProperty(value = "剩余开票金额")
    @ColumnWidth(16)
    private BigDecimal invoiceAmountsy;
    //收款金额
    @ExcelProperty(value = "收款金额")
    @ColumnWidth(15)
    private BigDecimal utilizedAmount;
    @ExcelProperty(value = "剩余收款金额")
    @ColumnWidth(16)
    private BigDecimal utilizedAmountsy;

    @ExcelProperty(value = "备注")
    @ColumnWidth(20)
    private String remark;

    @ExcelProperty(value = "状态")
    @ColumnWidth(15)
    private String statusLabel;


    @ExcelProperty(value = "财务审核状态")
    @ColumnWidth(16)
    private String financeStatusLabel;




}
