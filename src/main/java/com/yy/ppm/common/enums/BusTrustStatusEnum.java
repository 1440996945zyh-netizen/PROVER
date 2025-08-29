package com.yy.ppm.common.enums;

import lombok.Getter;

/**
 * 作业过程类型枚举
 */
@Getter
public enum BusTrustStatusEnum {

	DSH("10", "待发布"),
//	YSH("20", "已审核"),
	YFB("30", "已发布"),
	ZYZ("40", "作业中"),
	HX("50", "核销");

	BusTrustStatusEnum(String code, String comment) {
		this.code = code;
		this.comment = comment;
	}

	/**
	 * 枚举code 费目类型ID
	 **/
	private String code;

	/**
	 * 枚举注释 费目类型名称
	 **/
	private String comment;

}
