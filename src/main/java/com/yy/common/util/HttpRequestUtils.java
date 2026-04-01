package com.yy.common.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Http请求辅助类
 **/
public final class HttpRequestUtils {

	/**
	 * 代理服务器请求头
	 **/
	private static final String PROXY_HEADER = "X-Forwarded-For";

	private static final String X_REAL_IP = "X-Real-IP";

	private HttpRequestUtils() {

	}

	/**
	 * 获得客户真实IP(支持代理)
	 *
	 * @author
	 * @param request 请求对象
	 * @return Ip地址
	 **/
	public static String getRemoteAddrIp(HttpServletRequest request) {
		String header = request.getHeader(PROXY_HEADER);
		String realIp = request.getHeader(X_REAL_IP);
		if (StringUtils.isAllBlank(header, realIp)) {
			return request.getRemoteAddr();
		} else {
			if (StringUtils.isNotBlank(realIp)) {
				return realIp;
			}

			String[] ipArray = StringUtils.split(header, ",");
			if (ArrayUtils.isNotEmpty(ipArray)) {
				return ipArray[0];
			} else {
				return request.getRemoteAddr();
			}
		}
	}

	/**
	 * 获取当前网络ip
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request){
		String ipAddress = request.getHeader("x-forwarded-for");
		if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
			if(ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")){
				//根据网卡取本机配置的IP
				try {
					InetAddress inet = InetAddress.getLocalHost();
					ipAddress= inet.getHostAddress();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
		}
		//对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15
			if(ipAddress.indexOf(",")>0){
				ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));
			}
		}
		return ipAddress;
	}
}
