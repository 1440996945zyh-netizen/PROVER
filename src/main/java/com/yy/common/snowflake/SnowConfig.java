package com.yy.common.snowflake;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.util.str.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.net.InetAddress;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * 雪花算法主键生成器
 */
@Configuration
public class SnowConfig {

    private Integer workId;

    @Autowired
    private SnowflakeIp snowflakeIp; // 由 @ConfigurationProperties 自动注入

    @Bean
    public Snowflake snowflake() {
        // 确保 workId 已初始化
        if (workId == null) {
            throw new IllegalStateException("workId 未初始化，请检查配置");
        }
        return new Snowflake(workId.longValue(), 1L);
    }

    @PostConstruct
    private void getWorkId() {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            Object workIdObj = snowflakeIp.getIpdz().get(ip);
            String workIds = StringUtil.getString(workIdObj);
            workId = isBlank(workIds) ? 1 : Integer.parseInt(workIds);
        } catch (Exception e) {
            workId = 1;
        }
        System.out.println("Snowflake WorkId 已设置: " + workId);
    }
}
