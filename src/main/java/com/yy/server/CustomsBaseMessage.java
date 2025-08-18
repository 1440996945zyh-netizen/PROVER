package com.yy.server;

import java.io.UnsupportedEncodingException;

import com.yy.common.log.MicroLogger;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomsBaseMessage {

    private static final MicroLogger LOGGER = new MicroLogger(CustomsBaseMessage.class);
	private String message;// 原始数据
	private String messageStart;// 包头-- 固定值取0xE2 0x5C 0x4B 0x89
	private String messageEnd;// 包尾-- 0xFF 0xFF
	private String messageLength;// 总长-- 4个字节（包括和包尾包头的长度）
	private String messageChangZhanHao;// 场站号-- ASC码字符串 （10位）
	private String messageTongDaoHao;// 通道号-- ASC码字符串 （10位）
	private String messageJinChuKou;// 进出口标志-- ASC码字符串 （1位），’I’ 表示进场站  ‘E’表示出场站
	private String messageBiaoShiFu;// 标识符-- 用于区别监控，通常是0xff
	
	/**
	 * 	协议类型
		0x21	表示为采集数据传输	类型A	XMLInfoGather 标识符=1时表示采集数据不完整，要补采
		0x22	表示为平台控制数据返回操作	类型B	XMLInfoWLJKRet
		0x28	表示卡口返回平台放行回执指令	类型B 	XMLInfoExeResult
	 */
	private String messageType;// 消息类型-- 用于区别数据包的类型
	private String messageXMLLength;// XML流长度-- XML格式数据的长度（4 bytes）
	private String messageXML;// XML格式数据-- 为所传递的XML报文
	
	private byte[] messageStartBytes;// 包头-- 固定值取0xE2 0x5C 0x4B 0x89
	private byte[] messageEndBytes;// 包尾-- 0xFF 0xFF
	private byte[] messageLengthBytes;// 总长-- 4个字节（包括和包尾包头的长度）
	private byte[] messageChangZhanHaoBytes;// 场站号-- ASC码字符串 （10位）
	private byte[] messageTongDaoHaoBytes;// 通道号-- ASC码字符串 （10位）
	private byte[] messageJinChuKouBytes;// 进出口标志-- ASC码字符串 （1位），’I’ 表示进场站  ‘E’表示出场站
	private byte[] messageBiaoShiFuBytes;// 标识符-- 用于区别监控，通常是0xff
	private byte[] messageTypeBytes;// 消息类型-- 用于区别数据包的类型
	private byte[] messageXMLLengthBytes;// XML流长度-- XML格式数据的长度（4 bytes）
	private byte[] messageXMLBytes;// XML格式数据-- 为所传递的XML报文
	
	/**
	 * 获取byte数据
	 * @return
	 */
	public byte[] getByte() {
		byte[] res = new byte[9999];
		int index = 0;
		for (byte data: this.messageStartBytes) {
			res[index] = data;
			index++;
		}

		for (byte data: this.messageLengthBytes) {
			res[index] = data;
			index++;
		}
		
		for (byte data: this.messageTypeBytes) {
			res[index] = data;
			index++;
		}

		for (byte data: this.messageChangZhanHaoBytes) {
			res[index] = data;
			index++;
		}

		for (byte data: this.messageTongDaoHaoBytes) {
			res[index] = data;
			index++;
		}

		for (byte data: this.messageJinChuKouBytes) {
			res[index] = data;
			index++;
		}

		for (byte data: this.messageBiaoShiFuBytes) {
			res[index] = data;
			index++;
		}

		for (byte data: this.messageXMLLengthBytes) {
			res[index] = data;
			index++;
		}

		for (byte data: this.messageXMLBytes) {
			res[index] = data;
			index++;
		}

		for (byte data: this.messageEndBytes) {
			res[index] = data;
			index++;
		}
		
    	StringBuffer sb = new StringBuffer(res.length);
        String sTmp;

        for (int i = 0; i < res.length; i++) {
            sTmp = Integer.toHexString(0xFF & res[i]);
            if (sTmp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTmp.toUpperCase());
        }
    	
        LOGGER.error("发送数据：" + sb.toString());
		return res;
	}

	public String getStr() {
		StringBuffer sb = new StringBuffer();
		
		sb.append(messageStart);
		sb.append(messageLength);
		sb.append(messageType);
		sb.append(messageChangZhanHao);
		sb.append(messageTongDaoHao);
		sb.append(messageJinChuKou);
		sb.append(messageBiaoShiFu);
		sb.append(messageXMLLength);
		sb.append(messageXML);
		sb.append(messageEnd);
		
		return sb.toString();
	}
}
