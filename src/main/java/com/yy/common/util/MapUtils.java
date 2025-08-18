package com.yy.common.util;

import com.yy.common.util.str.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Map工具类
 *
 * @author
 **/
public final class MapUtils {

    /**
     *
     * @param key
     * @param value
     * @return
     */
    public static HashMap<String, Object> createMap(String key, Object value) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    /**
     * @param params :目标map,args:校验数组
     * @return boolean
     * @author 验证map设定非空字段是否为空
     **/
    public static boolean isEmptyMap(Map<String, ?> params, String[] args) {
        if (params == null || args == null || params.isEmpty() || args.length == 0) {
            return true;
        }

        boolean bool = false;
        for (String arg : args) {
            if (org.apache.commons.lang3.StringUtils.isBlank(StringUtil.getString(params.get(arg)))) {
                bool = true;
                break;
            }
        }
        return bool;
    }

    /**
     * @param params :目标map,args:校验数组
     * @return boolean
     * @author 验证map设定非空字段是否为空
     **/
    public static boolean isNumericMap(Map<String, ?> params, String[] args) {
        if (params == null || args == null || params.isEmpty() || args.length == 0) {
            return false;
        }

        boolean bool = true;
        for (String arg : args) {
            if (!org.apache.commons.lang3.StringUtils.isNumeric(StringUtil.getString(params.get(arg)))) {
                bool = false;
                break;
            }
        }
        return bool;
    }

    /**
     * 合并两个Map
     *
     * @param self
     * @param target
     * @author linqi
     */
    public static void putAll(Map<String, String> self, Map<String, String> target) {
        target.forEach((key, value) -> {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(value)) {
                self.put(key, value);
            }
        });
    }
}
