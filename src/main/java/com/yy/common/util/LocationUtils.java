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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * @Author linqi
 * @Description
 * @Date 2023-06-30 11:05
 */
public class LocationUtils {

    private static final Logger log = LoggerFactory.getLogger(LocationUtils.class);
    private static final Lazy<Searcher> SEARCHER = new Lazy<>(() -> {
        ClassPathResource resource = new ClassPathResource("xdbs/ip2region.xdb");
        Path tempPath = null;

        try (InputStream inputStream = resource.getInputStream()) {
            // 创建临时文件
            tempPath = Files.createTempFile("ip2region", ".xdb");

            // 复制资源到临时文件
            Files.copy(inputStream, tempPath, StandardCopyOption.REPLACE_EXISTING);

            // 加载文件内容到内存
            byte[] cBuff;
            try {
                cBuff = Searcher.loadContentFromFile(tempPath.toString());
            } catch (IOException e) {
                throw new IORuntimeException("Failed to load IP database from temp file: " + tempPath, e);
            }

            // 创建 Searcher 实例
            try {
                return Searcher.newWithBuffer(cBuff);
            } catch (IOException e) {
                throw new IORuntimeException("Failed to create searcher with cached buffer", e);
            }

        } catch (IOException e) {
            throw new IORuntimeException("Failed to initialize IP searcher", e);
        } finally {
            // 清理临时文件
            if (tempPath != null) {
                try {
                    Files.deleteIfExists(tempPath);
                } catch (IOException e) {
                    // 使用 SLF4J 日志记录详细错误
                    LoggerFactory.getLogger(Lazy.class)
                            .warn("Failed to delete temp file: {} - {}", tempPath, e.getMessage());
                }
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
