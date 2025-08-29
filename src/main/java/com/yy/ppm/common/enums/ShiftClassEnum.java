package com.yy.ppm.common.enums;

import lombok.Getter;

/**
 * 作业过程类型枚举
 */
@Getter
public enum ShiftClassEnum {

	YESTERDAY("0", "昨日"),
	TODAY("1", "今日"),
	TOMORROW("2", "明日");


	ShiftClassEnum(String code, String comment) {
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
