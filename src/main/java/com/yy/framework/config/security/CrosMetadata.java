package com.yy.framework.config.security;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CROS配置元数据 参考资料:
 * https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Access_control_CORS
 *
 * @author
 **/
@Component
@EnableConfigurationProperties({CrosMetadata.class})
@ConfigurationProperties(prefix = "cros.config")
@Setter
@Getter
@ToString
public class CrosMetadata {

	/**
	 * Cros路径匹配模式
	 **/
	private String pathPattern;

	/**
	 * 允许访问该资源的外域
	 **/
	private String origins;

	/**
	 * 首部字段用于预检请求的响应,其指明了实际请求所允许使用的 HTTP 方法。
	 **/
	private List<String> allowMethods;

	/**
	 * 允许浏览器访问的自定义响应头白名单
	 **/
	private List<String> exposedHeaders;

	/**
	 * 首部字段用于预检请求的响应,其指明了实际请求中允许携带的首部字段。
	 **/
	private List<String> allowHeaders;

	/**
	 * 预检请求的结果被缓存多久,单位:秒
	 **/
	private int maxAge;

	/**
	 * 是否支持附件凭证的预检请求
	 **/
	private boolean allowCredentials;
}
