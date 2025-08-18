package com.yy.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "ftp")
public class CustomsConfig {
    private String host;

    private String port;

    private String username;

    private String password;

    private String localTempPath;

    private String customsCode;

    private String unitCode;

    private String unitName;

    private String saveFilePath;
}
