package com.yy.server;

import java.nio.charset.Charset;

/**
 * 数据查改良字段
 * @author zcc
 *
 */
public class CustomsServerConsts {

	public static final String STRING_ENCODING = "UTF-8";

	public static final Charset STRING_CHARSET = Charset.forName(STRING_ENCODING);
	
	public static final int CUSTOMS_0XFF = 0xff;// 标识符
	
	/**
	 * ---------------------------------------已下为消息类型
	 */
	public static final String CUSTOMS_0X21 = "0x21";// 表示为采集数据传输	类型A	XMLInfoGather 标识符=1时表示采集数据不完整，要补采

	public static final String CUSTOMS_0X22 = "0x22";// 表示为平台控制数据返回操作	类型B	XMLInfoWLJKRet
	
	public static final String CUSTOMS_0X28 = "0x28";// 表示卡口返回平台放行回执指令	类型B 	XMLInfoExeResult
	
	
}