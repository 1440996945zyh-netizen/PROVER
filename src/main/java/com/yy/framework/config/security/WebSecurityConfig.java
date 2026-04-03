package com.yy.framework.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

import jakarta.annotation.Resource;

/**
 * Spring Security Config (更新为Spring Boot 3兼容版本)
 */
@EnableWebSecurity
@Configuration
public class WebSecurityConfig {


    @Resource
    private WhiteList whiteList;

    /**
     * Jwt过滤器
     */
    private final JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    /**
     * 跨域过滤器
     */
    private final CrosFilter crosFilter;

    /**
     * 自定义密码验证
     */
    private final CustomPasswordEncoder customPasswordEncoder;

    /**
     * 构造器注入
     */
    public WebSecurityConfig(
            JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter,
            CrosFilter crosFilter,
            CustomPasswordEncoder customPasswordEncoder) {
        this.jwtAuthenticationTokenFilter = jwtAuthenticationTokenFilter;
        this.crosFilter = crosFilter;
        this.customPasswordEncoder = customPasswordEncoder;
    }

    // 获取AuthenticationManager（认证管理器），登录时认证使用。
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 配置身份验证提供者
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(customPasswordEncoder);
        return authProvider;
    }

    /**
     * 配置安全过滤链
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 添加 JWT 过滤器
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        // 添加 CORS filter
        http.addFilterBefore(crosFilter, JwtAuthenticationTokenFilter.class);

        http
                .csrf(AbstractHttpConfigurer::disable)// NOSONAR 前后端分离JWT认证，CSRF防护冗余
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    // 1. 白名单URL允许匿名访问
                    for (String url : whiteList.getUrls()) {
                        auth.requestMatchers(url).permitAll();
                    }

                    // 2. 退出，登录后权限信息（roleList, Permissions)都有权限
                    auth
                            .requestMatchers("/api/internal/getPermissions").permitAll() // 前端用权限
                            .requestMatchers("/api/internal/logout").permitAll()
                            .requestMatchers("/api/internal/logoutApp").permitAll()
                            .requestMatchers("/api/internal/router/getRouters").permitAll();    // 左侧菜单

                    // 3. swagger相关信息允许任何用户访问
                    auth
                            .requestMatchers("/swagger-resources/**", "/webjars/**", "/v3/**",
                                    "/swagger-ui.html/**", "/swagger-ui/**", "/*/api-docs", "/druid/**").permitAll()
                            .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                            .anyRequest().authenticated();
                });

        return http.build();
    }

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
