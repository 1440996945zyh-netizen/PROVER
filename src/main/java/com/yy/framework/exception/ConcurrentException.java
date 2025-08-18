package com.yy.framework.exception;

/**
 * 并发异常
 *
 * @author gewx
 **/
public final class ConcurrentException extends RuntimeException {

    private static final long serialVersionUID = -2481884182369728336L;

    public ConcurrentException() {
        super();
    }

    public ConcurrentException(String message) {
        super(message);
    }

    public ConcurrentException(Throwable cause) {
        super(cause);
    }

    public ConcurrentException(String message, Throwable cause) {
        super(message, cause);
    }
}
