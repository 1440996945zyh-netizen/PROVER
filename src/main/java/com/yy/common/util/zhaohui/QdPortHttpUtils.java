package com.yy.common.util.zhaohui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import com.yy.common.enums.Constants;
import com.yy.common.enums.ContentType;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson2.JSON;

/**
 * 通用http发送方法
 * @author xing
 */
public class QdPortHttpUtils {

    private static final Logger log = LoggerFactory.getLogger(QdPortHttpUtils.class);
    
    /**
     * 向指定 URL 发送GET方法的请求
     *
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param)
    {
        return sendGet(url, param, Constants.UTF8);
    }

    /**
     * 向指定 URL 发送GET方法的请求
     *
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @param contentType 编码类型
     * @return 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param, String contentType)
    {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try
        {
            String urlNameString = StringUtils.isNotBlank(param) ? url + "?" + param : url;
            log.info("sendGet - {}", urlNameString);
            URL realUrl = new URL(urlNameString);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), contentType));
            String line;
            while ((line = in.readLine()) != null)
            {
                result.append(line);
            }
            log.info("recv - {}", result);
        }
        catch (ConnectException e)
        {
            log.error("调用HttpUtils.sendGet ConnectException, url=" + url + ",param=" + param, e);
        }
        catch (SocketTimeoutException e)
        {
            log.error("调用HttpUtils.sendGet SocketTimeoutException, url=" + url + ",param=" + param, e);
        }
        catch (IOException e)
        {
            log.error("调用HttpUtils.sendGet IOException, url=" + url + ",param=" + param, e);
        }
        catch (Exception e)
        {
            log.error("调用HttpsUtil.sendGet Exception, url=" + url + ",param=" + param, e);
        }
        finally
        {
            try
            {
                if (in != null)
                {
                    in.close();
                }
            }
            catch (Exception ex)
            {
                log.error("调用in.close Exception, url=" + url + ",param=" + param, ex);
            }
        }
        return result.toString();
    }

    /**
    * POST
    * @param url
    * @param params
    * @param contentType
    * @param token
    * @return
    * @return
    */
    public static String sendPost(String url, Map<String, Object> params, ContentType contentType, String token){
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(url);
        postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"UTF-8");
        postMethod.addRequestHeader("accept", "*/*");
        postMethod.addRequestHeader("connection", "Keep-Alive");

        if(StringUtils.isNotBlank(token)) {
            postMethod.addRequestHeader("Authorization", "Bearer " + token);
            postMethod.addRequestHeader("token", token);
        }

        postMethod.addRequestHeader("Content-Type", contentType.getComment());
        postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 150000);//设置超时时间
		if(1 == contentType.getCode()) {
	        for(String key : params.keySet()) {
	            postMethod.addParameter(key, params.get(key) != null? params.get(key).toString() : "");//添加请求参数
	        }
		} else if(2 == contentType.getCode()) {
			try {
				RequestEntity se = new StringRequestEntity(JSON.toJSONString(params), contentType.getComment(), "UTF-8");
				postMethod.setRequestEntity(se);
			} catch (UnsupportedEncodingException e) {
			}
		} else if(3 == contentType.getCode()) {
			try {
				RequestEntity se = new StringRequestEntity(params.get("data").toString(), contentType.getComment(), "UTF-8");
				postMethod.setRequestEntity(se);
			} catch (UnsupportedEncodingException e) {
			}
		}

        String res = "";
        try {
            int code = httpClient.executeMethod(postMethod);
        	InputStream inputStream = postMethod.getResponseBodyAsStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();
            String str = "";
            while((str = br.readLine()) != null){
            	stringBuffer.append(str);
            }
            res = stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}
