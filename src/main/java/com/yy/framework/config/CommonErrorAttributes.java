package com.yy.framework.config;

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.web.error.ErrorAttributeOptions;

/**
 * 统一错误格式
 */
@Component
public class CommonErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        // 替代原来的 includeStackTrace：判断是否包含栈跟踪
        boolean includeStackTrace = options.isIncluded(ErrorAttributeOptions.Include.STACK_TRACE);
        // 原有逻辑调整（如果涉及 includeStackTrace 的判断，改用上面的变量）
        this.addStatusThis(errorAttributes, webRequest);
        errorAttributes.put("success", false);
        // 如需处理栈跟踪，可继续调用 addErrorDetails 等方法
        // this.addErrorDetails(errorAttributes, webRequest, includeStackTrace);
        return errorAttributes;
    }

    private void addStatusThis(Map<String, Object> errorAttributes, RequestAttributes requestAttributes) {
        Integer status = (Integer)this.getAttributeThis(requestAttributes, "jakarta.servlet.error.status_code");
        if (status == null) {
            errorAttributes.put("code", 999);
        } else {
            errorAttributes.put("code", status);
            try {
                errorAttributes.put("msg", HttpStatus.valueOf(status).getReasonPhrase());
            } catch (Exception var5) {
                errorAttributes.put("msg", "Http Status " + status);
            }
        }
    }

    private <T> T getAttributeThis(RequestAttributes requestAttributes, String name) {
        return (T) requestAttributes.getAttribute(name, 0);
    }

}
