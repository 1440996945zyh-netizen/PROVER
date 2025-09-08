package com.yy.common.enums;

import lombok.Getter;

/**
 * Websocket枚举
 *
 **/
@Getter
public enum WebsocketEnum {

	CLIENT_MSG("30", "客户端给服务端发消息"),
	SERVER_MSG("40", "服务端给客户端发消息"),
	HEART_MSG("9", "心跳消息");

	WebsocketEnum(String code, String comment) {
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
