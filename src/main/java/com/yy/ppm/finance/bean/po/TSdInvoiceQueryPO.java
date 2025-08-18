package com.yy.ppm.finance.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName (TSdInvoiceQuery)PO
 * @Description
 * @createTime 2024年11月08日 10:34:00
 */
@Data
public class TSdInvoiceQueryPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -22817750929661952L;

    /**
     *
     */
    private Long id;
    /**
     *
     */
    private Long invoiceId;
    /**
     * 业务类型
     */
    private String businessType;
    /**
     * 订单号
     */
    private String orderNum;
    /**
     * 明细数
     */
    private String count;
    /**
     * 订单渠道
     */
    private String orderChannel;
    /**
     * 订单日期
     */
    private String orderDate;
    /**
     * 发票种类
     */
    private String invoiceTypeCode;
    /**
     * 开票类型
     */
    private String typeCode;
    /**
     * 特定要素
     */
    private String specialInvoiceMark;
    /**
     * 征税类型代码
     */
    private String taxInclusivePriceMark;
    /**
     * 含税标志
     */
    private String taxMark;
    /**
     * 交付方式途径
     */
    private String requestType;
    /**
     * 购买方手机
     */
    private String buyerMobileNum;
    /**
     * 购买方邮箱
     */
    private String buyerEmail;
    /**
     * 购买方微信
     */
    private String buyerWeChat;
    /**
     * 购买方会员号
     */
    private String buyerMemberId;
    /**
     * 合计金额
     */
    private String taxExclusiveTotalAmount;
    /**
     * 合计税额
     */
    private String taxTotalAmount;
    /**
     * 价税合计
     */
    private String taxInclusiveTotalAmount;
    /**
     * 备注
     */
    private String note;
    /**
     * 扣除额
     */
    private String deduction;
    /**
     * 收款人
     */
    private String payee;
    /**
     * 复核人
     */
    private String checker;
    /**
     * 开票人
     */
    private String invoiceClerk;
    /**
     * 红字确认单编号
     */
    private String infoFormNum;
    /**
     * 红字确认单uuid
     */
    private String applicationFormCode;
    /**
     * 原发票代码
     */
    private String originalInvoiceCode;
    /**
     * 原发票号码
     */
    private String originalInvoiceNo;
    /**
     * 原开票日期
     */
    private String originalIssueDate;
    /**
     * 购买方性质
     */
    private String buyerNature;
    /**
     * 购货单位识别号
     */
    private String buyerTaxId;
    /**
     * 购货单位名称
     */
    private String buyerName;
    /**
     * 购货单位地址
     */
    private String buyerAddrTel;
    /**
     * 购货单位电话
     */
    private String buyerTel;
    /**
     * 购货单位银行名称
     */
    private String buyerFinancialAccount;
    /**
     * 购货单位开户行账号
     */
    private String buyerAccount;
    /**
     * 销货单位识别号
     */
    private String sellerTaxId;
    /**
     * 销货单位名称
     */
    private String sellerName;
    /**
     * 销货单位地址
     */
    private String sellerAddrTel;
    /**
     * 销货单位电话
     */
    private String sellerTel;
    /**
     * 销货单位银行名称
     */
    private String sellerFinancialAccount;
    /**
     * 销货单位银行账号
     */
    private String sellerAccount;

}

