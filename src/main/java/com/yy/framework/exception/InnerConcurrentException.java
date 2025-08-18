package com.yy.framework.exception;

/**
 * @Auther linqi
 * @Description 内部并发异常
 * @Date 2023-08-28 15:29
 */
public final class InnerConcurrentException extends RuntimeException {

    public InnerConcurrentException() {
        super();
    }

    public InnerConcurrentException(String message) {
        super(message);
    }

    public InnerConcurrentException(Throwable cause) {
        super(cause);
    }

    public InnerConcurrentException(String message, Throwable cause) {
        super(message, cause);
    }
}
