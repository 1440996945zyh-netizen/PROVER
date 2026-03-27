package com.yy.ppm.equipment.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码工具类
 */
public class QRCodeUtil {

    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 300;
    private static final String FORMAT = "png";
    private static final Map<EncodeHintType, Object> HINTS = new HashMap<>();

    static {
        HINTS.put(EncodeHintType.CHARACTER_SET, "utf-8");
        HINTS.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        HINTS.put(EncodeHintType.MARGIN, 2);
    }

    /**
     * 生成二维码图片文件字节
     * @param content 二维码内容
     * @param width   宽
     * @param height  高
     */
    public static byte[] createQRCode(String content, int width, int height) throws WriterException, IOException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, HINTS);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, FORMAT, stream);
        return stream.toByteArray();
    }

    /**
     * 生成默认尺寸二维码图片
     * @param content 二维码内容
     * @return  byte[]
     */
    public static byte[] createQRCode(String content) throws WriterException, IOException {
        return createQRCode(content, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
}
