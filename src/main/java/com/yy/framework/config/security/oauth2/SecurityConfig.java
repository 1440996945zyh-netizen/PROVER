//package com.yy.framework.config.security.oauth2;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable) // 前后端分离通常禁用CSRF
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers(
//                                "/api/auth/login",
//                                "/api/oauth2/**",
//                                "/error"
//                        ).permitAll()
//                        .anyRequest().authenticated()
//                )
//                .oauth2Login(oauth2 -> oauth2
//                        .authorizationEndpoint(authorization -> authorization
//                                .baseUri("/api/oauth2/authorization") // 授权端点
//                        )
//                        .redirectionEndpoint(redirection -> redirection
//                                .baseUri("/api/login/oauth2/code/*") // 回调端点
//                        )
//                        .userInfoEndpoint(userInfo -> userInfo
//                                .userService(oauth2UserService()) // 用户信息服务
//                        )
//                        .successHandler(authenticationSuccessHandler()) // 认证成功处理器
//                )
//                .logout(logout -> logout
//                        .logoutUrl("/api/auth/logout")
//                        .logoutSuccessUrl("${AUTH_SERVER:http://localhost:9000}/logout")
//                        .invalidateHttpSession(true)
//                        .deleteCookies("JSESSIONID")
//                );
//        return http.build();
//    }
//
//    // OAuth2用户信息服务
//    @Bean
//    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
//        return new DefaultOAuth2UserService();
//    }
//
//    // 认证成功处理器
//    @Bean
//    public AuthenticationSuccessHandler authenticationSuccessHandler() {
//        return (request, response, authentication) -> {
//            // 认证成功后返回JSON响应
//            response.setContentType("application/json");
//            response.setCharacterEncoding("UTF-8");
//            response.getWriter().write("{\"status\":\"success\", \"message\":\"Authentication successful\"}");
//            response.getWriter().flush();
//        };
//    }
//}
