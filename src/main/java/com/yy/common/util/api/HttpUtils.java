package com.yy.common.util.api;

import com.yy.common.enums.ApiEnum;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 基于okHttp3 工具类
 *
 * @author
 **/
public final class HttpUtils {

    /**
     * 池化okHttp客户端组件,读取超时设置30秒
     **/
    private static final OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(30, TimeUnit.SECONDS)
            .build();

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static final MediaType X_WWW_FORM_URLENCODED = MediaType
            .get("application/x-www-form-urlencoded; charset=utf-8");

    /**
     * 发送GET请求
     * @param prefix 请求前缀
     * @param url
     * @return
     * @throws IOException
     *
     */
    public static String getRequest(ApiEnum prefix, String url) throws IOException {
        return getRequestHeaders(prefix, url, new HashMap<>());
    }

    /**
     * 发送GET请求
     * @param prefix 请求前缀
     * @param url
     * @param headersParam 请求头参数
     * @return
     * @throws IOException
     */
    public static String getRequestHeaders(ApiEnum prefix, String url, Map<String, String> headersParam) throws IOException {
        Request request = new Request.Builder().headers(setHheadersParam(headersParam).build()).url(prefix.code() + url).build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public static String getRequestHeader(ApiEnum prefix, String url, Map<String, String> headersParam) throws IOException {
        Request request = new Request.Builder().headers(setParam(headersParam).build()).url(prefix.code() + url).build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    /**
     * 发送POST请求 json 请求体
     * @param prefix 请求前缀
     * @param url
     * @param json json参数
     * @return
     * @throws IOException
     */
    public static String postJson(ApiEnum prefix, String url, String json, Map<String, String> headersParam) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().headers(setHheadersParam(headersParam).build()).url(prefix.code() + url).post(body).build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    /**
     * 发送POST请求
     * @param prefix
     * @param url
     * @param reqBody 请求体body
     * @return
     * @throws IOException
     */
    public static String postRequestBody(ApiEnum prefix, String url, String reqBody, Map<String, String> headersParam) throws IOException {
        RequestBody body = RequestBody.create(X_WWW_FORM_URLENCODED, reqBody);
        Request request = new Request.Builder().headers(setHheadersParam(headersParam).build()).url(prefix.code() + url).post(body).build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    /**
     * put 请求
     * @param prefix
     * @param url
     * @param json
     * @return
     * @throws IOException
     */
    public static String put(ApiEnum prefix, String url, String json, Map<String, String> headersParam) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().headers(setHheadersParam(headersParam).build()).url(prefix.code() + url).put(body).build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    /**
     * delete请求
     * @param prefix
     * @param url
     * @return
     * @throws IOException
     */
    public static String delete(ApiEnum prefix, String url, Map<String, String> headersParam) throws IOException {
        Request request = new Request.Builder().headers(setHheadersParam(headersParam).build()).url(prefix.code() + url).delete().build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    /**
     * 设置请求头参数
     * @param headersParam
     * @return
     */
    public static Headers.Builder setHheadersParam(Map<String, String> headersParam){
        //设置请求头信息
        Headers.Builder headers = new Headers.Builder();
        Set<String> set = headersParam.keySet();
        for (String key : set) {
            headers.set(key, headers.get(key));
        }
        return headers;
    }

    public static Headers.Builder setParam(Map<String, String> headersParam){
        //设置请求头信息
        Headers.Builder headers = new Headers.Builder();
        Set<String> set = headersParam.keySet();
        for (String key : set) {
            headers.set(key, headersParam.get(key));
        }
        return headers;
    }
}
