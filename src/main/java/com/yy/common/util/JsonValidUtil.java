package com.yy.common.util;

import com.alibaba.fastjson.JSONObject;

/**
 * JSON校验工具类
 * @author hukang
 */
public class JsonValidUtil {

    /**
     * 判断字符串是否为json格式
     *
     * @param jsonStr
     * @return
     */
    public static boolean isJson(String jsonStr) {
        try {
            JSONObject.parseObject(jsonStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
