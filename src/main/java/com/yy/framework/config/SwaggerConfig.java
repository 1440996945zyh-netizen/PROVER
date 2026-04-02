package com.yy.framework.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger配置类（Spring Boot 3使用SpringDoc OpenAPI替代Springfox）
 *
 * @author 扬奕软件
 */
@Configuration
public class SwaggerConfig {

    /**
     * 系统基础配置
     */
    private final ProjectConfig projectConfig;

    public SwaggerConfig(ProjectConfig projectConfig){
        this.projectConfig = projectConfig;
    }

    /**
     * 是否开启swagger
     */
    @Value("${swagger.enabled:true}")
    private boolean enabled;

    /**
     * 创建OpenAPI配置
     *
     * @return OpenAPI对象
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "Authorization";

        OpenAPI openAPI = new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(securityComponents(securitySchemeName));

        return openAPI;
    }

    /**
     * 创建API基本信息
     *
     * @return Info对象
     */
    private Info apiInfo() {
        return new Info()
                .title(projectConfig.getName())
                .description("用于" + projectConfig.getName() + "的接口信息")
                .contact(new Contact().name(projectConfig.getContact()))
                .version("版本号:" + projectConfig.getVersion());
    }

    /**
     * 创建安全组件配置
     *
     * @param securitySchemeName 安全方案名称
     * @return Components对象
     */
    private Components securityComponents(String securitySchemeName) {
        return new Components()
                .addSecuritySchemes(securitySchemeName,
                        new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"));
    }
}
