package com.yy.common.snowflake;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 终端id系统配置
 *
 * @author
 **/
@ConfigurationProperties(prefix = "snowflake")
@Setter
@Getter
@ToString
@Component
public class SnowflakeIp {

    private Map<String, Object> ipdz;
}
