package com.yy.common.magic;

import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

/**
 * 文件魔数辅助类
 *
 * @author
 **/
public final class FileMagicUtils {

    /**
     * 根据文件路径获取文件类型
     *
     * @param filePath 文件路径
     * @return 文件类型
     * @throws IOException
     * @author
     */
    public static String getFileType(String filePath) throws IOException {
        String fileHead = getFileHeader(filePath);
        if (StringUtils.isBlank(fileHead)) {
            return StringUtils.EMPTY;
        }

        return Stream.of(FileType.values()).filter(val -> fileHead.toUpperCase().startsWith(val.getValue()))
                .map(val -> val.getKey()).findFirst().orElse(StringUtils.EMPTY);
    }

    /**
     * 根据字节数组获取文件类型
     *
     * @param byteArray 字节数组
     * @return 文件类型
     * @throws IOException
     * @author
     */
    public static String getFileType(byte[] byteArray) throws IOException {
        String fileHead = bytes2hex(byteArray);
        if (StringUtils.isBlank(fileHead)) {
            return StringUtils.EMPTY;
        }

        return Stream.of(FileType.values()).filter(val -> fileHead.toUpperCase().startsWith(val.getValue()))
                .map(val -> val.getKey()).findFirst().orElse(StringUtils.EMPTY);
    }

    /**
     * 获取文件头
     *
     * @param filePath 文件路径
     * @return 16 进制的文件头信息
     * @throws IOException
     * @author gewex
     */
    private static String getFileHeader(String filePath) throws IOException {
        byte[] b = new byte[28];
        try (InputStream inputStream = new FileInputStream(filePath)) {
            inputStream.read(b, 0, 28);
        }
        return bytes2hex(b);
    }

    /**
     * 将字节数组转换成16进制字符串
     *
     * @param byteArray 字节数组
     * @return 字符串
     * @author
     **/
    private static String bytes2hex(byte[] byteArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : byteArray) {
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }
}
