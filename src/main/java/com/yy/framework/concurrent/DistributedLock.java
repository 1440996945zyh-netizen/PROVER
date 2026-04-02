package com.yy.framework.concurrent;

import com.yy.common.util.Assert;
import com.yy.framework.annotation.ThreadSafe;
import com.yy.framework.exception.ConcurrentException;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-30 10:28
 */
@ThreadSafe
public final class DistributedLock {

    @Getter
    public static class Builder {

        private RedisTemplate<String, String> store;

        private String key;

        private Integer timeout;

        private String tips;

        public Builder() {
            this.timeout = 5;
        }

        public Builder store(RedisTemplate<String, String> store) {
            this.store = store;
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder timeout(Integer timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder tips(String tips) {
            this.tips = tips;
            return this;
        }

        public DistributedLock build() {
            Assert.isNotNull(this.store, "Lock Store is Null！");
            Assert.isNotNull(this.key, "Lock Key is Null！");
            Assert.isNotNull(this.timeout, "Lock Timeout is Null！");
            return new DistributedLock(this);
        }
    }

    private final RedisTemplate<String, String> store;

    private final String key;

    private final Integer timeout;

    private final String tips;

    private DistributedLock(Builder builder) {
        this.store = builder.getStore();
        this.key = builder.getKey();
        this.timeout = builder.getTimeout();
        this.tips = builder.getTips();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public void run(Runnable runnable) {
        Exception exception = null;
        try {
            before();
            runnable.run();
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            if (!(exception instanceof ConcurrentException)) {
                after();
            }
        }
    }

    public <T> T run(Supplier<T> supplier) {
        Exception exception = null;
        try {
            before();
            return supplier.get();
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            if (!(exception instanceof ConcurrentException)) {
                after();
            }
        }
    }

    private void before() {
        Boolean result = store.opsForValue().setIfAbsent(this.key, "true", this.timeout, TimeUnit.SECONDS);
        boolean bool = Boolean.TRUE.equals(result);
        if (!bool) {
            throw new ConcurrentException(StringUtils.defaultIfBlank(this.tips, "并发异常，请联系管理员！"));
        }
    }

    private void after() {
        store.delete(this.key);
    }
}
