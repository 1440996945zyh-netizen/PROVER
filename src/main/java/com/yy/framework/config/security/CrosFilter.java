package com.yy.framework.config.security;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@Component
@Order(2)
public class CrosFilter extends OncePerRequestFilter {

    @Resource
    public CrosMetadata crosMetadata;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        resp.setHeader("Access-control-Allow-Origin", req.getHeader("Origin"));
        resp.setHeader("Access-Control-Allow-Methods",
                crosMetadata.getAllowMethods().stream().collect(Collectors.joining(", ")));
        resp.setHeader("Access-Control-Allow-Headers", req.getHeader("Access-Control-Request-Headers"));
        resp.setHeader("Access-Control-Expose-Headers",
                crosMetadata.getExposedHeaders().stream().collect(Collectors.joining(", ")));
        resp.setHeader("Access-Control-Allow-Credentials", String.valueOf(crosMetadata.isAllowCredentials()));
        resp.setHeader("Access-Control-Max-Age", String.valueOf(crosMetadata.getMaxAge()));

        if ("OPTIONS".equalsIgnoreCase(((HttpServletRequest) req).getMethod())) {
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(req, resp);
        }
    }

    @Override
    public void destroy() {
    }
}
