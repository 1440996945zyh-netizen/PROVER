package com.yy.common.util.zhaohui;

import java.rmi.ServerException;
import java.util.HashMap;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.fasterxml.jackson.annotation.JsonProperty;


import com.yy.common.enums.ContentType;
import lombok.Data;
import lombok.ToString;

public class ZhaoHuiDataUtils<T1, T2 extends HashMap<String, Object>> {

    
	public static String GetToken = "http://10.126.200.3:8013/api/Audience/GetToken";// 登录接口
	public static String getPileViewSrcOfThird = "http://10.126.200.3:8014/wharfMng/pileView/getPileViewSrcOfThird";// 查询货垛列表（地图图形）
	public static String writeFlowMachines = "http://172.18.5.51:8015/api/Device/writeFlowMachines";// 坐标位置写入朝辉服务

	public final static String USER_NAME = "yangyi";
	public final static String PASS_WORD = "YangYi135!";

/*	public TokenData getTokenData() {
		TokenData tokenData = SpringUtils.getBean(RedisCache.class).getCacheObject(USER_NAME);
		
		if(tokenData == null) {
			Map<String, Object> loginParams = new HashMap<String, Object>();
			loginParams.put("userName", USER_NAME);
			loginParams.put("password", PASS_WORD);

			String loginData = QdPortHttpUtils.sendPost(GetToken, loginParams, ContentType.CONTENT_TYPE_2, null);

			HttpResData<TokenData> loginHttpData = 
					JSONObject.parseObject(loginData, new TypeReference<HttpResData<TokenData>>() {
		    });
			
			tokenData = loginHttpData.getData();
			
			if(tokenData != null) {
				SpringUtils.getBean(RedisCache.class).setCacheObject(USER_NAME, tokenData, 50, TimeUnit.MINUTES);
			}
		}
		return tokenData;
	}*/
	
	/**
	 * 查询货垛列表
	 * @param param
	 * @return
	 * @throws ServerException 
	 */
	/*public List<T1> getPileViewSrcOfThird(T2 param, TypeReference<List<T1>> t1) throws ServerException {

		String result = QdPortHttpUtils.sendPost(getPileViewSrcOfThird, param, ContentType.CONTENT_TYPE_2, getTokenData().getAccessToken());
		
		HttpResData<Map<String, Object>> httpData = 
				JSONObject.parseObject(result, new TypeReference<HttpResData<Map<String, Object>>>() {
	    });
		
		if(200 != httpData.getCode()) {
	        throw new ServerException(httpData.getMsg());
		}
		if(httpData.getData() == null || httpData.getData().get("piles") == null) {
			return new ArrayList<T1>();
		} else {
			List<T1> dataList = 
					JSONObject.parseObject(JSONObject.toJSONString(httpData.getData().get("piles")), t1);
			return dataList;
		}
	}*/
	
	/**
	 * 查询货垛列表
	 * @param param
	 * @return
	 * @throws ServerException 
	 */
	public T1 writeFlowMachines(T2 param) throws ServerException {

		String result = QdPortHttpUtils.sendPost(writeFlowMachines, param, ContentType.CONTENT_TYPE_2, "");
		
		HttpResData<T1> httpData = 
				JSONObject.parseObject(result, new TypeReference<HttpResData<T1>>() {
	    });
		
		if(200 != httpData.getCode()) {
	        throw new ServerException(httpData.getMsg());
		}
		return httpData.getData();
	}
	
	@Data
	@ToString
	public static class HttpResData<T1>
	{
		private int code;
		private int bCode;
		private String msg;
	    private T1 data;
	}

    @Data
    public static class TokenData{

    	@JsonProperty("access_token")
    	private String accessToken;
    	@JsonProperty("expires_in")
    	private String expiresIn;
    	@JsonProperty("token_type")
    	private String tokenType;
    	@JsonProperty("refresh_token")
    	private String refreshToken;
    }
}
