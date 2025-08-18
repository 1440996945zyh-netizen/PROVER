package com.yy.ppm.master.bean.po;


import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName 客户开票信息(MCustomerInvoce)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月05日 16:29:00
 */
@Data
public class MCustomerInvoicePO extends BasePO implements Serializable {

    private static final long serialVersionUID = 903705644381085479L;

    /** 主键id */
    private Long id;
    /** 客户CODE */
    private String customerCode;
    /** 开票名称 */
    private String invoiceName;
    /** 开票名称 */
    private String invAddress;
    /** 电话 */
    private String invTel;
    /** 开户行 */
    private String invBank;
    /** 开户行号 */
    private String invBankNo;
    /** 纳税人识别号 */
    private String invTaxNo;
    /** 发票类型 0专票 2普票 */
    private Long invKindFlag;
    /** 备注 */
    private String remark;


}

