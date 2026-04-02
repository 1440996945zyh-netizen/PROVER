package com.yy.common.util;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import org.lionsoul.ip2region.xdb.Searcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author linqi
 * @Description
 * @Date 2023-06-30 11:05
 */
public class LocationUtils {

    private static final Logger log = LoggerFactory.getLogger(LocationUtils.class);
    public static Lazy<Searcher> SEARCHER = new Lazy<>(() -> {
        ClassPathResource resource = new ClassPathResource("xdbs/ip2region.xdb");

        File tempFile;
        try {
            tempFile = File.createTempFile("ip2region", ".xdb");
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }

        try (InputStream inputStream = resource.getInputStream(); FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            IoUtil.copy(inputStream, outputStream);

            byte[] cBuff;
            try {
                cBuff = Searcher.loadContentFromFile(tempFile.getPath());
            } catch (IOException e) {
                throw new IORuntimeException("failed to load content: " + e);
            }

            Searcher searcher;
            try {
                searcher = Searcher.newWithBuffer(cBuff);
            } catch (IOException e) {
                throw new IORuntimeException("failed to create content cached searcher: " + e);
            }

            return searcher;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            if (!tempFile.delete()) {
                log.warn("Failed to delete file: {}",tempFile);
            }
        }
    });

    /**
     * 根据提供的ip获取位置信息
     *
     * @param ip
     * @return 国家|区域|省份|城市|ISP
     */
    public static String getLocation(String ip) {
        try {
            return SEARCHER.get().search(ip);
        } catch (Exception e) {
            return "";
        }
    }
}
