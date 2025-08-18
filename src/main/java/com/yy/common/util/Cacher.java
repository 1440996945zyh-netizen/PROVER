package com.yy.common.util;

import com.yy.framework.annotation.ThreadSafe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @Auther linqi
 * @Description 基于计算的缓存
 * @Date 2023-10-30 11:23
 */
@ThreadSafe
public class Cacher<T, U> {

    private final Function<T, U> calculation;

    private final Map<T, U> value;

    public Cacher(Function<T, U> calculation) {
        this.calculation = calculation;
        this.value = new ConcurrentHashMap<>();
    }

    public U value(T t) {
        return value.computeIfAbsent(t, this.calculation);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
