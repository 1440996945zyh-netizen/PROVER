package com.yy.common.util;

/**
 * 内容解密
 * 
 * @author NEC
 *
 */
public class Decrypt {
	static {
		// 加载加密/解密模块
		System.loadLibrary("decrypt");
	}

	/**
	 * 解密用户名密码
	 * 
	 * @param name
	 *            用户名
	 * @param pswd
	 *            密码
	 * @return 解密后用户名密码
	 *         <p>
	 *         返回值[0]:用户名 <br>
	 *         返回值[1]:密码
	 *         </p>
	 */
	public static native String[] decryptUserPswd(String name, String pswd);
}
