//package com.yy.framework.config.security.oauth2;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Collections;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController {
//
//    /**
//     * 登录入口 - 返回认证服务器授权URL
//     */
//    @GetMapping("/login")
//    public Map<String, String> login() {
//        return Collections.singletonMap(
//                "authorizationUrl",
//                "/api/oauth2/authorization/sso-auth"
//        );
//    }
//
//    /**
//     * 获取当前用户信息
//     */
//    @GetMapping("/user")
//    public Map<String, Object> getCurrentUser(@AuthenticationPrincipal OAuth2User user) {
//        if (user == null) {
//            return Collections.singletonMap("error", "Not authenticated");
//        }
//        return Collections.singletonMap("user", user.getAttributes());
//    }
//
//    /**
//     * 受保护的资源示例
//     */
//    @GetMapping("/protected-resource")
//    public Map<String, String> protectedResource(@AuthenticationPrincipal OAuth2User user) {
//        String username = user != null ? user.getName() : "Guest";
//        return Collections.singletonMap(
//                "message",
//                "Protected resource accessed by: " + username
//        );
//    }
//}
