package com.yy.ppm.finance.webserver.util;

import com.google.common.collect.Maps;
import com.yy.framework.exception.BusinessRuntimeException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.charset.Charset;

public class PostmanFormData {
    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://example.com/api/resource"); // 替换为你的URL

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("key1", "value1"));
        urlParameters.add(new BasicNameValuePair("key2", "value2"));

        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(urlParameters);
        httpPost.setEntity(formEntity);

        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            String responseString = EntityUtils.toString(entity);
            System.out.println(responseString);
        }

        httpClient.close();
    }

    public static String sendFormData(HashMap<String,String> map,String url) {
        try{
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
//          HttpPost httpPost = new HttpPost("http://112.53.79.20:8086/receipt/importdetail/addInvoice");
            List<NameValuePair> urlParameters = new ArrayList<>();
            map.forEach((k,v)->urlParameters.add(new BasicNameValuePair(k, v)));
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(urlParameters, Charset.forName("UTF-8"));
            httpPost.setEntity(formEntity);
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String responseString = "";
            if (entity != null) {
                responseString = EntityUtils.toString(entity, Charset.forName("UTF-8")); // 如果服务器响应也是UTF-8编码，可以指定字符集
            }else{
                return "远得数电接口返回值为空，请联系远得管理人员";
            }
            httpClient.close();
            return responseString;
        }catch (IOException e){
            throw new BusinessRuntimeException(e.getMessage());
        }
    }
}