package com.yy.ppm.finance.bean.dto;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author wangxd
 * @version 1.0.0
 * @ClassName 客户预缴(TFdBankCustomerPrepayment)DTO
 * @Description
 * @createTime 2024年01月01日 11:30:00
 */
@Data
public class TFdBankCustomerPaymentDTO implements Serializable {

    private static final long serialVersionUID = -76573950601194395L;
    /**
     * 明细类型
     */
    @ExcelProperty(value = "明细类型", index = 1)
    private String detailType;
    /**
     * 金额
     */
    @ExcelProperty(value = "金额", index = 2)
    private BigDecimal amount;

    /**
     * 扣款日期
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建日期", index = 6)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date paymentTime;

    @ExcelProperty(value = "业务编号", index = 0)
    private String businessNo;

    @ExcelProperty(value = "创建人", index = 5)
    private String createByName;

    @ExcelProperty(value = "船名航次", index = 4)
    private String shipVoyageName;

    @ExcelProperty(value = "作业公司", index = 3)
    private String companyName;


}
