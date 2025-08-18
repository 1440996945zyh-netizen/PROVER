package com.yy.ppm.finance.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

/**
 * 发票返回信息
 */
@Getter
@Setter
@ToString
public class SzhdzInvoiceRequest {

    /**
     * 发票号码
     */
    @JsonProperty("invoiceNo")
    private String invoiceNo;
    /**
     * 开票日期
     */
    @JsonProperty("issueDate")
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String issueDate;
    /**
     * 入库日期
     */
    @JsonProperty("createTime")
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTime;
    /**
     * 特定要素
     */
    @JsonProperty("specialInvoiceMark")
    private String specialInvoiceMark;
    /**
     * 文件信息
     */
    @JsonProperty("fileUrlInfo")
    private FileUrlInfo fileUrlInfo;
    /**
     * 销货信息
     */
    @JsonProperty("sellerInfo")
    private SellerInfo sellerInfo;
    /**
     * 购方信息
     */
    @JsonProperty("buyerInfo")
    private BuyerInfo buyerInfo;
    /**
     * 征税类型代码
     */
    @JsonProperty("taxInclusivePriceMark")
    private String taxInclusivePriceMark;
    /**
     * 发票信息
     */
    @JsonProperty("invoiceLines")
    private InvoiceLines invoiceLines;
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
     * 随机数
     */
    @JsonProperty("randomNum")
    private String randomNum;

    @Getter
    @Setter
    public class FileUrlInfo {
        /**
         * 可视化文件获取地址
         */
        @JsonProperty("fileUrl")
        private String fileUrl;
        /**
         * xml文件获取地址
         */
        @JsonProperty("fileUrlXml")
        private String fileUrlXml;

    }

    /**
     * 销货信息
     */
    @Getter
    @Setter
    public class SellerInfo {
        /**
         * 销货单位识别号
         */
        @JsonProperty("sellerTaxID")
        private String sellerTaxID;
        /**
         * 销货单位名称
         */
        @JsonProperty("sellerName")
        private String sellerName;
        /**
         * 销货单位名称
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
         * 销货单位银行账号
         */
        @JsonProperty("sellerAccount")
        private String sellerAccount;
    }

    @Getter
    @Setter
    public class BuyerInfo {

        /**
         * 购货单位识别号
         */
        @JsonProperty("buyerTaxID")
        private String buyerTaxID;
        /**
         * 购货单位名称
         */
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
    public class InvoiceLines {

        /**
         * 数目
         */
        @JsonProperty("count")
        private String count;
        /**
         * 条目
         */
        @JsonProperty("group")
        private List<Group> group;

    }

    @Getter
    @Setter
    public class Group {
        /**
         * id
         */
        @JsonProperty("id")
        private String id;
        /**
         * 发票行性质
         */
        @JsonProperty("natureLines")
        private String natureLines;
        /**
         * 项目名称
         */
        @JsonProperty("item")
        private String item;
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
        @JsonProperty("quantity")
        private String quantity;
        /**
         * 单价
         */
        @JsonProperty("price")
        private String price;
        /**
         * 金额
         */
        @JsonProperty("amount")
        private String amount;
        /**
         * 税率
         */
        @JsonProperty("taxScheme")
        private String taxScheme;
        /**
         * 税额
         */
        @JsonProperty("taxAmount")
        private String taxAmount;
        /**
         * 含税标志
         */
        @JsonProperty("taxMark")
        private String taxMark;
        /**
         * 商品和服务税收分类合并编码
         */
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
        @JsonProperty("zeroTaxSchemeMark")
        private String zeroTaxSchemeMark;

    }

}

