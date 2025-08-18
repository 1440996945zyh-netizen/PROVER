package com.yy.framework.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 拦截绕过gateway的请求
 */
@Component
@Slf4j
public class WebMvcInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 这里可以添加您的拦截逻辑
        // 例如：验证请求是否绕过了网关
        /*
        String gatewayHeader = request.getHeader("X-Gateway-Proxy");
        if (StringUtils.isEmpty(gatewayHeader)) {
            log.warn("检测到绕过网关的请求: {}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        */

        return true; // 返回true继续处理，返回false中断请求
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 这里可以添加请求完成后的处理逻辑
        // 例如：记录请求完成日志
        /*
        if (ex != null) {
            log.error("请求处理过程中发生异常: {}", request.getRequestURI(), ex);
        } else {
            log.info("请求处理完成: {}", request.getRequestURI());
        }
        */

        // 调用父类实现（在接口中是默认方法，通常不需要调用）
        // super.afterCompletion(request, response, handler, ex);
    }
}
