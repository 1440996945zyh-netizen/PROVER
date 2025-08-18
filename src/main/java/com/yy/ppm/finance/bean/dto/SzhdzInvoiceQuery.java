package com.yy.ppm.finance.bean.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * 山东港口-乐企
 * 数字化电子发票
 * (SzhdzInvoiceDTO)DTO
 * @date 2024-11-04 00:00:00
 */

@Getter
@Setter
@ToString
public class SzhdzInvoiceQuery {
    /**
     * 业务类型
     */
    @JsonProperty("businessType")
    private String businessType;
    /**
     * 订单号
     */
    @JsonProperty("orderNum")
    private String orderNum;

    /**
     * 订单渠道
     */
    @JsonProperty("orderChannel")
    private String orderChannel;
    /**
     * 订单日期
     */
    @JsonProperty("orderDate")
    private String orderDate;
    /**
     * 发票种类
     */
    @NotBlank(message = "发票种类不能为空")
    @JsonProperty("invoiceTypeCode")
    private String invoiceTypeCode;
    /**
     * 开票类型
     */
    @NotBlank(message = "开票类型不能为空")
    @JsonProperty("typeCode")
    private String typeCode;
    /**
     * 特定要素
     */
    @NotBlank(message = "特定要素不能为空")
    @JsonProperty("specialInvoiceMark")
    private String specialInvoiceMark;
    /**
     * 征税类型代码
     */
    @NotBlank(message = "征税类型代码不能为空")
    @JsonProperty("taxInclusivePriceMark")
    private String taxInclusivePriceMark;
    /**
     * 含税标志
     */
    @NotBlank(message = "含税标志不能为空")
    @JsonProperty("taxMark")
    private String taxMark;
    /**
     * 交付方式途径
     */
    @JsonProperty("requestType")
    private String requestType;
    /**
     * 购买方手机
     */
    @JsonProperty("buyerMobileNum")
    private String buyerMobileNum;
    /**
     * 购买方邮箱
     */
    @JsonProperty("buyerEmail")
    private String buyerEmail;
    /**
     * 购买方微信
     */
    @JsonProperty("buyerWeChat")
    private String buyerWeChat;
    /**
     * 购买方会员号
     */
    @JsonProperty("buyerMemberId")
    private String buyerMemberId;
    /**
     * 购买信息
     */
    @JsonProperty("buyerInfo")
    private BuyerInfo buyerInfo;
    /**
     * 销货信息
     */
    @JsonProperty("sellerInfo")
    private SellerInfo sellerInfo;

    @JsonProperty("invoiceProductList")
    private InvoiceProductList invoiceProductList;
    /**
     * 合计金额
     */
    @JsonProperty("taxExclusiveTotalAmount")
    private String taxExclusiveTotalAmount;
    /**
     * 合计税额
     */
    @JsonProperty("taxTotalAmount")
    private String taxTotalAmount;
    /**
     * 价税合计
     */
    @NotBlank(message = "价税合计不能为空")
    @JsonProperty("taxInclusiveTotalAmount")
    private String taxInclusiveTotalAmount;
    /**
     * 扣除额
     */
    @JsonProperty("deduction")
    private String deduction;
    /**
     * 备注
     */
    @JsonProperty("note")
    private String note;
    /**
     * 是否展示销售方银行账号标签
     */
    @JsonProperty("displaySellerFinancialAccount")
    private String displaySellerFinancialAccount;
    /**
     * 是否展示购买方银行账号标签
     */
    @JsonProperty("displayBuyerFinancialAccount")
    private String displayBuyerFinancialAccount;
    /**
     * 收款人
     */
    @JsonProperty("payee")
    private String payee;
    /**
     * 复核人
     */
    @JsonProperty("checker")
    private String checker;
    /**
     * 开票人
     */
    @JsonProperty("invoiceClerk")
    private String invoiceClerk;
    /**
     * 红字确认单编号
     */
    @JsonProperty("infoFormNum")
    private String infoFormNum;
    /**
     * 红字确认单uuid
     */
    @JsonProperty("applicationFormCode")
    private String applicationFormCode;
    /**
     * 原发票代码
     */
    @JsonProperty("originalInvoiceCode")
    private String originalInvoiceCode;
    /**
     * 原发票号码
     */
    @JsonProperty("originalInvoiceNo")
    private String originalInvoiceNo;
    /**
     * 开票日期
     */
    @JsonProperty("originalIssueDate")
    private String originalIssueDate;

    @Getter
    @Setter
    public static class BuyerInfo {
        /**
         * 购买方性质
         */
        @NotBlank(message = "购买方性质不能为空")
        @JsonProperty("buyerNature")
        private String buyerNature;
        /**
         * 购货单位识别号
         */
        @NotBlank(message = "购货单位识别号不能为空")
        @JsonProperty("buyerTaxID")
        private String buyerTaxID;
        /**
         * 购货单位名称
         */
        @NotBlank(message = "购货单位名称不能为空")
        @JsonProperty("buyerName")
        private String buyerName;
        /**
         * 购货单位地址
         */
        @JsonProperty("buyerAddrTel")
        private String buyerAddrTel;
        /**
         * 购货单位电话
         */
        @JsonProperty("buyerTel")
        private String buyerTel;
        /**
         * 购货单位银行名称
         */
        @JsonProperty("buyerFinancialAccount")
        private String buyerFinancialAccount;
        /**
         * 购货单位开户行账号
         */
        @JsonProperty("buyerAccount")
        private String buyerAccount;
    }

    @Getter
    @Setter
    public static class SellerInfo {

        /**
         * 销货单位识别号
         */
        @NotBlank(message = "销货单位识别号不能为空")
        @JsonProperty("sellerTaxID")
        private String sellerTaxID;
        /**
         * 销货单位名称
         */
        @NotBlank(message = "销货单位名称不能为空")
        @JsonProperty("sellerName")
        private String sellerName;
        /**
         * 销货单位地址
         */
        @JsonProperty("sellerAddrTel")
        private String sellerAddrTel;
        /**
         * 销货单位电话
         */
        @JsonProperty("sellerTel")
        private String sellerTel;
        /**
         * 销货单位银行名称
         */
        @JsonProperty("sellerFinancialAccount")
        private String sellerFinancialAccount;
        /**
         * 销货单位开户行账号
         */
        @JsonProperty("sellerAccount")
        private String sellerAccount;
    }

    @Getter
    @Setter
    public static class InvoiceProductList{

        /**
         * 明细行数
         */
        @NotBlank(message = "明细行数不能为空")
        @JsonProperty("count")
        private String count;
        /**
         * 明细
         */
        @JsonProperty("group")
        private List<Group> group;

    }

    @Getter
    @Setter
    public static class Group{
        /**
         * 明细序号
         */
        @NotBlank(message = "明细序号不能为空")
        @JsonProperty("id")
        private String id;
        /**
         * 发票行性质
         */
        @NotBlank(message = "发票行性质不能为空")
        @JsonProperty("natureLines")
        private String natureLines;
        /**
         * 项目名称
         */
        @NotBlank(message = "项目名称不能为空")
        @JsonProperty("item")
        private String item;
        /**
         * 商品服务简称
         */
        @JsonProperty("shorterItem")
        private String shorterItem;
        /**
         * 规格型号
         */
        @JsonProperty("specification")
        private String specification;
        /**
         * 单位
         */
        @JsonProperty("measurementDimension")
        private String measurementDimension;
        /**
         * 数量
         */
        @JsonProperty("quantity ")
        private String quantity;
        /**
         * 单价
         */
        @JsonProperty("price")
        private String price;
        /**
         * 金额
         */
        @NotBlank(message = "金额不能为空")
        @JsonProperty("amount")
        private String amount;
        /**
         * 税率
         */
        @NotBlank(message = "税率不能为空")
        @JsonProperty("taxScheme")
        private String taxScheme;
        /**
         * 税额
         */
        @JsonProperty("taxAmount")
        private String taxAmount;
        /**
         * 商品和服务税收，分类合并编码
         */
        @NotBlank(message = "商品和服务税收，分类合并编码不能为空")
        @JsonProperty("productCode")
        private String productCode;
        /**
         * 企业商品自编码
         */
        @JsonProperty("selfProductCode")
        private String selfProductCode;
        /**
         * 优惠政策标识
         */
        @JsonProperty("zerotaxSchemeMark")
        private String zerotaxSchemeMark;
        /**
         * 对应蓝字发票明细序号
         */
        @JsonProperty("blueLetterInvoiceID")
        private String blueLetterInvoiceID;
    }

}

