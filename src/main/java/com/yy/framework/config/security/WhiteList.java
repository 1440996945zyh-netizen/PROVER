package com.yy.framework.config.security;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableConfigurationProperties({WhiteList.class})
@ConfigurationProperties(prefix = "yy.white-list")
@Setter
@Getter
@ToString
public class WhiteList {

    private List<String> accounts;

    private List<String> urls;

}
