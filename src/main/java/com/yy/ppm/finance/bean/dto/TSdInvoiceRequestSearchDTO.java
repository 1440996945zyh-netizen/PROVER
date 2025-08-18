package com.yy.ppm.finance.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @ClassName (TSdInvoiceRequest)SearchDTO
 * @author makejava
 * @version 1.0.0
 * @Description TODO
 * @createTime 2024年11月07日 10:14:00
 */
@Data
public class TSdInvoiceRequestSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 834597889781251426L;
    
/**主键id*/
    private BigDecimal id;
/**发票号码*/
    private BigDecimal invoiceNo;
/**开票日期*/
    private Date issueDate;
/**特定要素*/
    private String specialInvoiceMark;
/**征税类型代码*/
    private String taxInclusivePriceMark;
/***/
    private String invoiceLines;
/**合计金额*/
    private String taxExclusiveTotalAmount;
/**合计税额*/
    private String taxTotalAmount;
/**价税合计*/
    private String taxInclusiveTotalAmount;
/**扣除额*/
    private String deduction;
/**备注*/
    private String note;
/**收款人*/
    private String payee;
/**复核人*/
    private String checker;
/**开票人*/
    private String invoiceClerk;
/**随机数*/
    private String randomNum;
/**可视化文件获取地址*/
    private String fileUrl;
/**xml文件获取地址*/
    private String fileUrlXml;
/**销货单位识别号*/
    private String sellerTaxId;
/**销货单位名称*/
    private String sellerName;
/**销货单位地址*/
    private String sellerAddrTel;
/**销货单位电话*/
    private String sellerTel;
/**销货单位银行名称*/
    private String sellerFinancialAccount;
/**销货单位开户行账号*/
    private String sellerAccount;
/**购货单位识别号*/
    private String buyerTaxId;
/**购货单位名称*/
    private String buyerName;
/**购货单位地址*/
    private String buyerAddrTel;
/**购货单位电话*/
    private String buyerTel;
/**购货单位银行名称*/
    private String buyerFinancialAccount;
/**购货单位开户行账号*/
    private String buyerAccount;
/**明细数量，子表*/
    private String count;
/***/
    private String createByName;
/***/
    private String updateByName;
}

