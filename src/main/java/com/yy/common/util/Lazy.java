package com.yy.common.util;

import cn.hutool.core.exceptions.UtilException;
import com.yy.framework.annotation.ThreadSafe;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * 实例延迟加载
 *
 * @Author linqi
 * @Description
 * @Date 2023-02-23 15:43
 */
@ThreadSafe
public final class Lazy<T> {

    private volatile T value;

    private volatile Supplier<T> factory;

    private boolean inited;

    private final Object lock = new Object();

    /**
     * 创建一个从提供的工厂获取其值的Lazy实例
     *
     * @param factory
     */
    public Lazy(Supplier<T> factory) {
        this.factory = factory;
    }

    /**
     * 创建一个包含给定值的Lazy实例
     *
     * @param value
     */
    public Lazy(T value) {
        this.value = value;
        this.inited = true;
    }

    /**
     * 惰性求值
     *
     * @return
     */
    public T get() {
        if (inited) {
            return value;
        }

        if (factory == null) {
            throw new UtilException("Lazy实例化异常");
        }

        synchronized (lock) {
            if (!inited) {
                value = factory.get();
                inited = true;
            }
            return value;
        }
    }

    @Override
    public String toString() {
        return Objects.toString(get());
    }

    @Override
    public boolean equals(Object object) {
        return (object instanceof Lazy) && Objects.equals(get(), ((Lazy<?>) object).get());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(get());
    }
}