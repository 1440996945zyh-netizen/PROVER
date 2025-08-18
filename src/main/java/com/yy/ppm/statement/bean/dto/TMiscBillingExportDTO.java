package com.yy.ppm.statement.bean.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class TMiscBillingExportDTO  {
    @ExcelProperty(value = "费目",index = 0)
    @ColumnWidth(value = 20)
    private String rateName;
    @ExcelProperty(value = "结算单号",index = 1)
    @ColumnWidth(value = 20)
    private String statementNo;
    @ExcelProperty(value = "作业过程",index = 2)
    @ColumnWidth(value = 20)
    private String processName;
    @ExcelProperty(value = "客户名称",index = 3)
    @ColumnWidth(value = 20)
    private String customerName;
    @ExcelProperty(value = "船名航次",index = 4)
    @ColumnWidth(value = 20)
    private String shipVoyage;
    @ExcelProperty(value = "泊位",index =5)
    @ColumnWidth(value = 20)
    private String berthName;
    @ExcelProperty(value = "货名",index =6)
    @ColumnWidth(value = 20)
    private String cargoName;
    @ExcelProperty(value = "费率",index =7)
    @ColumnWidth(value = 20)
    private BigDecimal rate;
    @ExcelProperty(value = "数量一",index =8)
    @ColumnWidth(value = 20)
    private BigDecimal billQuantity;
    @ExcelProperty(value = "数量二",index =9)
    @ColumnWidth(value = 20)
    private BigDecimal billQuantity2;
    @ExcelProperty(value = "总额",index =10)
    @ColumnWidth(value = 20)
    private BigDecimal amountMoney;
    @ExcelProperty(value = "创建人",index =11)
    @ColumnWidth(value = 20)
    private String createByName;
    @ExcelProperty(value = "计费人",index =12)
    @ColumnWidth(value = 20)
    private String statementByName;
    @ExcelProperty(value = "驳回人",index =13)
    @ColumnWidth(value = 20)
    private String rejectByName;
    @ExcelProperty(value = "驳回时间",index =14)
    @ColumnWidth(value = 20)
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd hh:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
    private Date rejectTime;
    @ExcelProperty(value = "驳回原因",index =15)
    @ColumnWidth(value = 20)
    private String rejectReason;
    @ExcelProperty(value = "状态",index =16)
    @ColumnWidth(value = 20)
    private String status;

    @ExcelIgnore private String statementStatus;

    @ExcelIgnore private String feeType;
    /** 纳税人识别号 */
    @ExcelIgnore private String tin;
    /** 开户行 */
    @ExcelIgnore private String bank;
    /** 银行账号 */
    @ExcelIgnore private String bankAccount;
    /** 企业电话 */
    @ExcelIgnore private String telephoneNumber;
    /**
     * 客户的联系电话
     */
    @ExcelIgnore  private String contactNumber;
    /** 联系人姓名 */
    @ExcelIgnore private String contact;
    /** 企业地址 */
    @ExcelIgnore private String address;
    @ExcelIgnore
    private String isMainIncome;


   /**
    * 审核人信息
    */@ExcelIgnore
    private String reviewByName;
    @ExcelIgnore
    private String reviewTime;
    @ExcelIgnore
   private String confirmByName;
    @ExcelIgnore

    private Long VoyageId;
    @ExcelIgnore

   private BigDecimal taxRate;
    @ExcelIgnore

   private BigDecimal taxAmount;

}
