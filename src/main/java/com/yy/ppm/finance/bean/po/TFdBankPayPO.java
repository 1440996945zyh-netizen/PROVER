package com.yy.ppm.finance.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author rzg
 * @version 1.0.0
 * @ClassName 付款银行维护(TFdBankPay)PO
 * @Description
 * @createTime 2023年09月13日 16:23:00
 */
@Data
public class TFdBankPayPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -14354855659409499L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 作业公司ID
     */
    private Long companyId;
    /**
     * 作业公司名称
     */
    private String companyName;
    /**
     * 开户行
     */
    private String openAccountBank;
    /**
     * 银行代码
     */
    private String bankCode;
    /**
     * 银行中文名称
     */
    private String bankName;
    /**
     * 银行英文名称
     */
    private String bankNameEnglish;
    /**
     * 银行账户号码
     */
    private Long bankNumber;
    /**
     * 银行类别编码EAS
     */
    private Long bankTypeCode;
    /**
     * 银行类别名称EAS
     */
    private String bankTypeName;
    /**
     * 银行账户编码EAS
     */
    private Long bankAccountCode;
    /**
     * 银行账户名称EAS
     */
    private String bankAccountName;
    /**
     * 货币代码
     */
    private Long currencyCode;
    /**
     * 货币代码名称
     */
    private String currencyName;

}

