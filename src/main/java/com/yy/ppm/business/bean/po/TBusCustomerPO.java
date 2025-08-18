package com.yy.ppm.business.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName 合同表(TBusCustomer)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月29日 13:09:00
 */
@Data
public class TBusCustomerPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 930796769503949136L;

    /** 主键ID */
    private Long id;
    /** 渤海通ID */
    private String bhtId;
    /** 客户代码 */
    private String customerCode;
    /** 客户名称（判重） */
    private String customerName;
    /** 客户简称（判重） */
    private String customerShortName;
    /** 客户类型，1国内企业2国外企业3个人 */
    private String customerTypeCode;
    /** 发票抬头 */
    private String invoice;
    /** 纳税人识别号 */
    private String tin;
    /** 开户行 */
    private String bank;
    /** 银行账号 */
    private String bankAccount;
    /** 企业地址 */
    private String address;
    /** 企业电话 */
    private String telephoneNumber;
    /** 联系人姓名 */
    private String contact;
    /** 联系人电话 */
    private String contactNumber;
    /** 联系人地址 */
    private String contactAddress;
    /** 0：停用；；1：待审核 9：驳回  10：审批通过*/
    private String status;
    /** 备注 */
    private String remark;
    /** 审核者-ID */
    private Long approvalBy;
    /** 审核者-姓名 */
    private String approvalName;
    /** 审核时间 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date approvalTime;
    /** 驳回意见 */
    private String idea;
    /** 客户属性 */
    private String customerPropertyNames;
    /** 助记码 */
    private String shorthandCode;
    /** 金蝶客户名称 */
    private String customerCodeEas;
    /** 金蝶客户代码 */
    private String customerNameEas;

    /**
     * 是否授信 0否 1 是
     */
    private Long isCredit;
    /**
     * 税务服务发票(1增值税普通发票，2增值税专用发票，2增值税普通电子发票)（字典TAX_INVOICE )
     */
    private String taxationInvoice;
    /**
     * 税务服务发票(1增值税普通发票，2增值税专用发票，2增值税普通电子发票)
     */
    private Long taxationInvoiceCode;

    //0是否 1是 是
    private String isStations;
    //0否 1 是
    private String dayNightPlanControl;
}

