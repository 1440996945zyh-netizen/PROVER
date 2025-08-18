package com.yy.ppm.finance.bean.po;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoicePO {
	
	// 与业务系统数据对接
	private String number;// 业务流水号
	private String busNo;// 订单编号
	private String organ;// 组织机构代码
	private String clientName;// 购方名称
	private String clientTaxCode;// 购方税号
	private String clientBankAccount;// 购方银行及账号
	private String clientAddressPhone;// 购方地址及电话
	private String clientPhone;// 客户手机号
	private String clientMail;// 客户邮箱
	private String billType;// 发票性质
	private String infoKind;// 发票类型
	private String notes;// 发票备注
	private String invoiceCode;// 蓝字发票代码
	private String invoiceNo;// 蓝字发票号码
	private String invoicer;// 开票人
	private String checker;// 复核人
	private String cashier;// 收款人
	private String allMoney;// 是否全部现金
	private double sumMoney;// 现金总金额
	private String goodsName;//货物名称
	private String standard;// 规格型号
	private String unit;// 计量单位
	private double num;// 数量
	private double price;// 含税单价
	private double amount;//含税金额־
	private double taxRate;// 税率
	private double taxAmount;// 税额
	private double aigo;// 折扣金额
	private double aigoTax;// 折扣税额
	
	private String goodsGroup;// 货物类别
	private String goodsNoVer;// 编码版本号
	private String goodsTaxNo;// 税收分类编码
	private String taxPre;// 是否享受优惠政策
	private String taxPreCon;// 优惠政策类型
	private String zeroTax;// 零税率标识
	private String goodsTaxName;// 税收分类编码简称
	private String specialInvoice;// 特殊票种
	
	private String retCode;//返回代码
	private String retMsg;// 提示信息
	private String time;//开票日期
	private String pdfurl;//电子发票下载地址
	private String pdfMsg;//生成 pdf 文件失败原因

}
