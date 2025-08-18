//package com.yy.framework.config.security.oauth2;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import java.util.Collections;
//import java.util.Map;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    /**
//     * 处理认证异常
//     */
//    @ExceptionHandler(AuthenticationException.class)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    public Map<String, String> handleAuthenticationException(AuthenticationException ex) {
//        return Collections.singletonMap("error", "Authentication failed: " + ex.getMessage());
//    }
//
//    /**
//     * 处理其他异常
//     */
//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public Map<String, String> handleGeneralException(Exception ex) {
//        return Collections.singletonMap("error", "Internal server error: " + ex.getMessage());
//    }
//}
