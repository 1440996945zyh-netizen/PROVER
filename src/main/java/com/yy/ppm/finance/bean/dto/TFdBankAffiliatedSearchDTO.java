package com.yy.ppm.finance.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 关联银行维护(TFdBankAffiliated)SearchDTO
 * @Description TODO
 * @createTime 2023年09月13日 15:16:00
 */
@Data
public class TFdBankAffiliatedSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -87514906444025876L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 付款方式
     */
    private String paymentMethodCode;
    /**
     * 付款方式NAME
     */
    private String paymentMethodName;
    /**
     * 付款类型
     */
    private String paymentTypeCode;
    /**
     * 付款类型NAME
     */
    private String paymentTypeName;
    /**
     * 银行编码
     */
    private String bankCode;
    /**
     * 银行ID
     */
    private Long bankId;
    /**
     * 银行名称中文
     */
    private String bankName;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 公司ID
     */
    private Long companyId;
    /**
     * 是否默认
     */
    private Long isDefault;
    /**
     * 创建者-姓名
     */
    private String createByName;
    /**
     * 更新者-姓名
     */
    private String updateByName;
}

