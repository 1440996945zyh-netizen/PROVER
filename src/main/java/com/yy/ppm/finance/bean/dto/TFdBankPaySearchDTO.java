package com.yy.ppm.finance.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author rzg
 * @version 1.0.0
 * @ClassName 付款银行维护(TFdBankPay)SearchDTO
 * @Description TODO
 * @createTime 2023年09月13日 16:23:00
 */
@Data
public class TFdBankPaySearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -84071516833379406L;

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
    /**
     * 创建者-姓名
     */
    private String createByName;
    /**
     * 更新者-姓名
     */
    private String updateByName;
}

