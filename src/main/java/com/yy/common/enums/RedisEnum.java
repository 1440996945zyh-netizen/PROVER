package com.yy.common.enums;

import lombok.Getter;

/**
 * redis缓存key枚举
 *
 **/
@Getter
public enum RedisEnum {

	USER_INFO("userInfo:YY_ACCNO_", "用户明细前缀"),

	TOKEN_EXPIRES_ACCOUNT_PC("tokenExpires:TOKEN_EXPIRES_ACCOUNT_PC_", "PC端token时间戳"),
	TOKEN_EXPIRES_ACCOUNT_APP("tokenExpires:TOKEN_EXPIRES_ACCOUNT_APP_", "APP端token时间戳"),

	ONLINE_ACCOUNTS_PC("ONLINE_ACCOUNTS_PC", "登录活跃用户PC"),

	BHT_ACCOUNT_TOKEN("BHT_ACCOUNT_TOKEN", "渤海通TOKEN"),
	ONLINE_ACCOUNTS_APP("ONLINE_ACCOUNTS_APP", "登录活跃用户APP");

	RedisEnum(String code, String comment) {
		this.code = code;
		this.comment = comment;
	}

	/**
	 * 编码
	 **/
	private String code;

	/**
	 * 注释
	 **/
	private String comment;
}
