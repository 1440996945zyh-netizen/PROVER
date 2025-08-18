package com.yy.ppm.finance.enums;

import com.yy.framework.exception.BusinessRuntimeException;
import lombok.Getter;

/**
 * 作业过程类型枚举
 */
@Getter
public enum CompanyEnum {
//	山东省港口集团潍坊港有限公司
//	潍坊港区集装箱码头有限公司
//	潍坊港区散货码头有限公司
	GKJT(1677243099354632192l,
			"山东省港口集团潍坊港有限公司",
			"91370700576634196A",
			"山东省潍坊市滨海经济技术开发区疏港公路与沿海公路一期十字路口东2000米路南",
			"0536-7532952",
			"潍坊工行营业部  1607001709201042489"),

	SHMT(1677242790846795776l,
			"潍坊港区散货码头有限公司",
			"91370700778433376A",
			"潍坊滨海经济开发区央子街办以北25公里处",
			"0536-7577866",
			"招商银行潍坊分行  999019339610701"),

	JZXMT(1677242971285753856l,
			"潍坊港区集装箱码头有限公司",
			"91370700MA3M7TUM0M",
			"山东省潍坊市滨海区央子街办以北25公里处",
			"0536-7577866",
			"中国银行潍坊分行  219536945130");

	CompanyEnum(Long id,String name, String taxNum, String address, String telephone, String account) {
		this.id = id;
		this.name = name;
		this.taxNum = taxNum;
		this.address = address;
		this.telephone = telephone;
		this.account = account;
	}

	public static CompanyEnum getCompanyEnum(String name){
		for(CompanyEnum companyEnum : CompanyEnum.values()){
			if(name.equals(companyEnum.getName())){
				return companyEnum;
			}
		}
		return null;
	}

	public static CompanyEnum getCompanyEnumById(Long id){
		for(CompanyEnum companyEnum : CompanyEnum.values()){
			if(id.equals(companyEnum.getId())){
				return companyEnum;
			}
		}
		return null;
	}





	/**
	 * 名称
	 **/
	private Long id;

	/**
	 * 名称
	 **/
	private String name;

	/**
	 * 企业税号
	 **/
	private String taxNum;

	/**
	 * 企业地址
	 **/
	private String address;

	/**
	 * 企业电话
	 **/
	private String telephone;

	/**
	 * 银行信息
	 **/
	private String account;

}
