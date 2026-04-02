package com.yy.common.util;

import cn.hutool.core.exceptions.UtilException;
import com.yy.framework.annotation.ThreadSafe;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@ThreadSafe
public final class Lazy<T> {

    private final AtomicReference<T> valueRef = new AtomicReference<>();
    private final Supplier<T> factory;   // factory 不可变，无需 volatile

    /**
     * 创建一个从提供的工厂获取其值的Lazy实例
     */
    public Lazy(Supplier<T> factory) {
        this.factory = factory;
    }

    /**
     * 创建一个包含给定值的Lazy实例（已初始化）
     */
    public Lazy(T value) {
        this.factory = null;
        this.valueRef.set(value);
    }

    /**
     * 惰性求值（线程安全，无锁快速路径）
     */
    public T get() {
        T val = valueRef.get();
        if (val != null) {
            return val;
        }

        // 未初始化，需要同步创建
        synchronized (this) {
            val = valueRef.get();
            if (val == null) {
                if (factory == null) {
                    throw new UtilException("Lazy实例化异常");
                }
                val = factory.get();
                valueRef.set(val);
            }
            return val;
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
