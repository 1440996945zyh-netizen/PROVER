package com.yy.common.excel.export.utils;

import cn.hutool.core.net.URLEncoder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * @Author linqi
 * @Description
 * @Date 2023-05-18 13:55
 */
public class ResponseUtils {

    public static final String MIME_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String MIME_PDF = "application/pdf";
    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyyMMddHHmmss");
    private static final String SUFFIX_XLSX = ".xlsx";
    private static final String SUFFIX_PDF = ".pdf";

    /**
     * 处理响应, 返回excel
     *
     * @param response
     * @param filename
     */
    public static void compliantWithExcel(HttpServletResponse response, String filename) {
        filename = URLEncoder.createDefault().encode(filename + " " + new DateTime(new Date()).toString(FORMATTER) + SUFFIX_XLSX, UTF_8);
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
        response.setContentType(MIME_XLSX);
    }

    /**
     * 处理响应, 返回pdf
     */
    public static void compliantWithPdf(HttpServletResponse response, String filename) {
        filename = URLEncoder.createDefault().encode(filename + " " + new DateTime(new Date()).toString(FORMATTER) + SUFFIX_PDF, UTF_8);
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
        response.setContentType(MIME_PDF);
    }

    /**
     * 重置响应处理
     *
     * @param response
     */
    public static void resetCompliant(HttpServletResponse response) {
        response.setHeader("Content-Disposition", EMPTY);
        response.setContentType(null);
    }
}
