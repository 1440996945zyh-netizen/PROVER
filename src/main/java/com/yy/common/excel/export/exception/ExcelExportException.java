package com.yy.common.excel.export.exception;

/**
 * @Author linqi
 * @Description excel导出异常
 * @Date 2023-05-18 13:55
 */
public final class ExcelExportException extends RuntimeException {

    public ExcelExportException() {
        super();
    }

    public ExcelExportException(String msg) {
        super(msg);
    }
}
