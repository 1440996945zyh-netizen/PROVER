package com.yy.ppm.finance.bean.po;


import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 关联银行维护(TFdBankAffiliated)PO
 * @Description
 * @createTime 2023年09月13日 15:16:00
 */
@Data
public class TFdBankAffiliatedPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -98471410588789803L;

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
     * 银行名称中文
     */
    private String bankName;
    /**
     * 银行主表
     */
    private Long bankId;
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

}

