package com.yy.ppm.finance.bean.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.page.PageParameter;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 发票表(TFdInvoice)SearchDTO
 * @Description TODO
 * @createTime 2023年09月15日 20:22:00
 */
@Data
public class TFdInvoiceSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 889488289308374802L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 作业公司
     */
    private String companyName;
    /**
     * 作业公司id
     * 查询详情结算单
     */
    private Long companyId;
    /**
     * 系统发票编号
     */
    private String sysInvoiceCode;
    /**
     * 发票编号
     */
    private Long invoiceCode;
    /**
     * 付款人(客户名称
     * 查询结算单
     */
    private Long customerId;
    /**
     * 付款人name(客户名称name
     */
    private String customerName;
    /**
     * 金额
     */
    private BigDecimal invoiceAmount;
    /**
     * 状态
     */
    private Long status;
    /**
     * 发票类型
     * 查询结算单
     */
    private Long invoiceTypeCode;
    /**
     * 发票类型name
     */
    private String invoiceTypeName;
    /**
     * 税务服务发票（字典TAX_INVOICE
     */
    private String taxationInvoice;
    /**
     * 税务发票编号
     */
    private Long taxationInvoiceCode;
    /**
     * 开票日期
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date invoiceTime;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建者-姓名
     */
    private String createByName;
    /**
     * 更新者-姓名
     */
    private String updateByName;

    /**
     * 船名航次
     */
    private Long shipvoyageItemId;

    /**
     * scn
     */
    private  String scn;
    /**
     * 结算单编号
     */
    private String statementNo;

    /**
     * 出港开始时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String startTime;
    /**
     * 出港结束时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endTime;



    private String isSj;


}

