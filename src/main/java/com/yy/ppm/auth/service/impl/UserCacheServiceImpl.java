package com.yy.ppm.auth.service.impl;

import com.yy.common.enums.RedisEnum;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.str.StringUtil;
import com.yy.ppm.auth.service.AuthService;
import com.yy.ppm.auth.service.LoginService;
import com.yy.ppm.auth.service.UserCacheService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 用户缓存实现
 *
 * @author
 **/
@Service
public class UserCacheServiceImpl implements UserCacheService {

	@Value("${spring.application.name}")
	private String applicationName;

	/**
	 * 日志组件
	 */
	private static final MicroLogger LOGGER = new MicroLogger(UserCacheServiceImpl.class);

	private final RedisTemplate<String, String> redisTemplate;

	public UserCacheServiceImpl(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}


	@Override
	public void cleanCacheByAccNo(String accNo) {
		final String methodName = "cleanCacheByAccNo";
		LOGGER.enter(methodName, "业务执行");

		redisTemplate.delete(applicationName + ":" + RedisEnum.USER_INFO.getCode() + accNo);

		LOGGER.exit(methodName, StringUtils.EMPTY);
	}

	@Override
	public String getCacheById(String keyId) {
		try {
			return redisTemplate.opsForValue().get(applicationName + ":" + keyId);
		} catch (Exception ex) {
			LOGGER.error("缓存查询失败,ex: " + StringUtil.getErrorText(ex));
			return StringUtils.EMPTY;
		}
	}

	@Override
	public String getCacheById(String keyId, String mapkey) {
		try {
			return StringUtil.getString(redisTemplate.opsForHash().get(applicationName + ":" + keyId, mapkey));
		} catch (Exception ex) {
			LOGGER.error("缓存查询失败,ex: " + StringUtil.getErrorText(ex));
			return StringUtils.EMPTY;
		}
	}

	@Override
	public void setCache(String keyId, String cacheVal) {
		try {
			redisTemplate.opsForValue().set(applicationName + ":" + keyId, cacheVal);
		} catch (Exception ex) {
			LOGGER.error("缓存设置失败,ex: " + StringUtil.getErrorText(ex));
		}
	}

	@Override
	public void setCache(String keyId, String mapkey, String cacheVal) {
		try {
			redisTemplate.opsForHash().put(applicationName + ":" + keyId, mapkey, cacheVal);
		} catch (Exception ex) {
			LOGGER.error("缓存设置失败,ex: " + StringUtil.getErrorText(ex));
		}
	}

	/**
	 * 清空缓存的用户信息，当角色和菜单变化时进行清空，防止用户鉴权时使用老数据
	 */
	@Override
	public void clearUserInfo() {
		try {
			Set<String> keys = redisTemplate.keys((applicationName + ":" + RedisEnum.USER_INFO.getCode()).concat("*"));

			if (keys != null) {
				for (String key : keys) {
					redisTemplate.delete(key);
				}
			}
		} catch (Exception ex) {
			LOGGER.error("清空缓存userInfo失败, ex: " + StringUtil.getErrorText(ex));
		}
	}



}
