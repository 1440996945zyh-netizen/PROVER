package com.yy;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

/**
 * 扬奕散（件）杂货港口生产管理云平台
 */
@SpringBootApplication(scanBasePackages = {"com.yy"},
		exclude = DataSourceAutoConfiguration.class)
@MapperScan(basePackages = { "com.yy.ppm.**.mapper" })
@EnableAsync // 启用异步执行
@EnableScheduling
public class MainApplication{

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

	/**
	 * 系统启动默认设置
	 *
	 * @return void
	 **/
	public static void init() {
		// 设置时区
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
	}
}
