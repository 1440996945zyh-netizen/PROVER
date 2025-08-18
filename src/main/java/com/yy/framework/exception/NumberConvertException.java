package com.yy.framework.exception;

public class NumberConvertException extends RuntimeException {

    private static final long serialVersionUID = 7511533816287895914L;

    public NumberConvertException() {
        super();
    }

    public NumberConvertException(String msg) {
        super(msg);
    }
}
