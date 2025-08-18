package com.yy.common.excel.export.exception;

/**
 * @Author linqi
 * @Description pdf转换异常
 * @Date 2023-05-18 13:55
 */
public final class PdfConversionException extends RuntimeException {

    public PdfConversionException() {
        super();
    }

    public PdfConversionException(String msg) {
        super(msg);
    }
}
