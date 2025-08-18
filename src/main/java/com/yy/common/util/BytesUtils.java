package com.yy.common.util;

import org.apache.commons.lang3.StringUtils;

public class BytesUtils {
	
	/**
	 * 数组转字符串
	 * @param bytes
	 * @return
	 */
	public static String bytesToString(byte[] bytes) {

		if (null == bytes || bytes.length == 0) {
			return "";
		} else {
			StringBuilder temp = new StringBuilder(bytes.length * 2);
			for (int i = 0; i < bytes.length; i++) {
				// 高四位 1111 0000
				temp.append((bytes[i] & 0xf0) >>> 4);
				// 低四位 0000 1111
				temp.append(bytes[i] & 0x0f);
			}
			return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp.toString().substring(1)
					: temp.toString();
		}
	}

	/**
	 * 数组转Integer
	 * @param bytes
	 * @return
	 */
	public static int bytesToInteger(byte[] data) {
		if (data == null) {
			return 0;
		}

		int result = 0;
		int len = data.length;
		int temp;
		for (int i = 0; i < len; i++) {
			temp = (len - 1 - i) * 8;
			if (temp == 0) {
				result += (data[i] & 0x0ff);
			} else {
				result += (data[i] & 0x0ff) << temp;
			}
		}
		return result;
	}

	/**
	 * 截取数组
	 * @param bytes
	 * @return
	 */
	public static String[] subByte(String[] data, int startIndex, int lenth) {
		try {
			String[] temp = new String[lenth];
			System.arraycopy(data, startIndex, temp, 0, lenth);
			return temp;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 十进制数组转16进制数组
	 * @param b
	 * @return
	 */
	public static String[] toHexStringByte(byte[] b) {
		String[] hexByte = new String[b.length];
	    for (int i = 0; i < b.length; ++i) {
	    	hexByte[i] = toHexString(b[i]);
	    }
	    return hexByte;
	}
	
	public static String toHexString(byte b) {
	    String s = Integer.toHexString(b & 0xFF);
	    if (s.length() == 1) {
	        return "0" + s.toUpperCase();
	    } else {
	        return s.toUpperCase();
	    }
	}
	
	/**
	 * 字符串转16进制字符串
	 * @param input
	 * @return
	 */
	public static String stringToHex(String input) {
	    StringBuilder hex = new StringBuilder();
	    for (int i = 0; i < input.length(); i++) {
	        hex.append(String.format("0x%02X", (int) input.charAt(i)));
	    }
	    return hex.toString();
	}
	
	public static byte[] strToASCII(String str) {
		
		if(StringUtils.isBlank(str)) {
			return null;
		}
		byte[] res = new byte[str.length()];
		for (int i = 0; i < str.length(); i++) {
			
            char c = str.charAt(i); // 获取每个字符
            res[i] = (byte)(int)c; // 将字符转换为对应的ASCII码值
        }
		return res;
	}
	
	public static void main(String[] args) {
			System.out.println(stringToHex("0945"));
	}
}
