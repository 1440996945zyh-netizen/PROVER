package com.yy.common.excel.export.utils;

import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.Workbook;
import com.yy.common.excel.export.exception.PdfConversionException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @Author linqi
 * @Description
 * @Date 2023-05-18 13:55
 */
public class PdfUtils {

    /**
     * 转换自excel
     *
     * @param bytes
     * @return
     */
    public static byte[] convertFromExcel(byte[] bytes) {
        InputStream is = new ByteArrayInputStream(bytes);

        Workbook workbook;
        try {
            workbook = new Workbook(is);
        } catch (Exception e) {
            throw new PdfConversionException("pdf转换失败，检查是否为合法excel文件");
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        PdfSaveOptions options = new PdfSaveOptions();
        options.setOnePagePerSheet(true);

        try {
            workbook.save(os, options);
        } catch (Exception ignored) {
        }

        return os.toByteArray();
    }
}
