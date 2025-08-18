package com.yy.ppm.auth.service;

/**
 * 用户缓存服务类
 *
 * @author
 **/
public interface UserCacheService {

	/**
	 * 清理账号缓存
	 *
	 * @author
	 * @param accNo 账号
	 * @return void
	 **/
	void cleanCacheByAccNo(String accNo);

	/**
	 * 获取缓存
	 *
	 * @author
	 * @param keyId 缓存Id
	 * @return 缓存数据
	 **/
	String getCacheById(String keyId);

	/**
	 * 获取缓存
	 *
	 * @author
	 * @param keyId  缓存Id
	 * @param mapkey mapkey
	 * @return 缓存数据
	 **/
	String getCacheById(String keyId, String mapkey);

	/**
	 * 设置缓存
	 *
	 * @author
	 * @param keyId    缓存Id
	 * @param cacheVal 缓存数据
	 * @return void
	 **/
	void setCache(String keyId, String cacheVal);

	/**
	 * 设置缓存
	 *
	 * @author
	 * @param keyId    缓存Id
	 * @param mapkey   mapkey
	 * @param cacheVal 缓存数据
	 * @return void
	 **/
	void setCache(String keyId, String mapkey, String cacheVal);

	/**
	 * 清空用户信息
	 */
	void clearUserInfo();
}
