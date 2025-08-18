package com.yy.common.util;


import java.util.function.Supplier;

/**
 * 延迟加载工具类
 *
 * @author gewx
 **/
public final class LazyUtils {

    /**
     * 延迟加载工具方法
     *
     * @param object     即刻对象
     * @param lazyObject 延迟对象
     * @return 数据域
     * @author gewx
     **/
    public static <T> T get(Supplier<T> object, Supplier<T> lazyObject) {
        T val = object.get();
        if (val == null) {
            val = lazyObject.get();
        }
        return val;
    }
}
