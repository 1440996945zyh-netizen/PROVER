package com.yy.ppm.common.controller;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.yy.common.log.MicroLogger;
import com.yy.ppm.machine.controller.MLocationHistoryController;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;






/**
 * @author czk
 * @version 1.0.0
 * @ClassName 服务器日志删除(LogFileDeleteTask)LogFileDeleteTask
 * @Description
 * @createTime 2024年06月12日 16:05:00
 */

@Configuration
@EnableScheduling
public class LogFileDeleteTask {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(MLocationHistoryController.class);

    protected static final List<String> FILE_PATH_LIST = Arrays.asList(
            "C:\\wfppm-logs\\info\\info-log-",
            "C:\\wfppm-logs\\warn\\warn-log-",
            "C:\\wfppm-logs\\error\\error-log-",
            "C:\\wfppm-logs\\debug\\debug-log-",
            "C:\\wfppm-logs\\trace\\trace-log-");

    /**
     * 定时删除日志文件,每天中午12点执行一次,一次性删除10天日志
     * @param
     * @return 是否成功
     */
    @Scheduled(cron="0 0 12 * * ?")
    public static void delete() {
        LOGGER.enter("定时任务开始删除日志文件");
        DateTime beginDate = DateUtil.offset( new DateTime(), DateField.DAY_OF_YEAR, -35);
        DateTime endDate = DateUtil.offset( new DateTime(), DateField.DAY_OF_YEAR, -33);
        List<DateTime> rangDateList = DateUtil.rangeToList(beginDate,endDate,DateField.DAY_OF_YEAR);
        rangDateList.forEach(date->{
            String dateStr = DateUtil.format(date,"yyyy-MM-dd");
            FILE_PATH_LIST.forEach(filePath->{
                // 指定要删除的文件路径
                deleteOldFiles(filePath+dateStr+".log",dateStr);
            });
        });
        LOGGER.exit("定时任务结束删除日志文件");
    }


    private static void deleteOldFiles(String filePath,String dateStr) {
        try {
            // 将文件路径转换为Path对象
            Path path = Paths.get(filePath);
            // 如果文件存在，则删除它
            if (Files.deleteIfExists(path)) {
                LOGGER.info(dateStr+"-日志文件已成功删除");
            } else {
                LOGGER.info(dateStr+"-日志文件不存在");
            }
        } catch (IOException e) {
            // 处理可能发生的IO异常
            LOGGER.error(dateStr + e.getMessage());
        }
    }

//
//
//
//
//    private static final String DIRECTORY_PATH = "C:\\wfppm-logs\\info\\"; // 指定目录路径
//    private static final long MAX_FILE_AGE_DAYS = 30; // 设置文件最大存活时间（小时）
//
//    public static void main(String[] args) {
//        deleteOldFiles();
//    }
//
//
//
//
//    private static void deleteOldFiles() {
//        File directory = new File(DIRECTORY_PATH);
//        if (directory.exists() && directory.isDirectory()) {
//            LocalDateTime cutoff = LocalDateTime.now().minus(MAX_FILE_AGE_DAYS, ChronoUnit.DAYS);
//
//            for (File file : directory.listFiles()) {
////                if (file.isFile() && file.lastModified() < cutoff.toInstant(ZoneOffset).toEpochMilli()) {
////                if (file.isFile() && file.lastModified() < cutoff.toInstant(java.time.ZoneOffset.systemDefault()).toEpochMilli()) {
//                    if (file.isFile() && file.lastModified() < System.currentTimeMillis()) {
//                    try {
//                        Path filePath = Paths.get(file.toURI());
//                        Files.deleteIfExists(filePath);
//                        LOGGER.info("Deleted file: " + file.getName());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }

}

