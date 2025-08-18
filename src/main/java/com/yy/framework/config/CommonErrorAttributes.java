package com.yy.framework.config;

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.ServletException;
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
        this.addStatus(errorAttributes, webRequest);
        errorAttributes.put("success", false);
        // 如需处理栈跟踪，可继续调用 addErrorDetails 等方法
        // this.addErrorDetails(errorAttributes, webRequest, includeStackTrace);
        return errorAttributes;
    }

    private void addStatus(Map<String, Object> errorAttributes, RequestAttributes requestAttributes) {
        Integer status = (Integer)this.getAttribute(requestAttributes, "jakarta.servlet.error.status_code");
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

    private <T> T getAttribute(RequestAttributes requestAttributes, String name) {
        return (T) requestAttributes.getAttribute(name, 0);
    }

    private void addErrorDetails(Map<String, Object> errorAttributes, WebRequest webRequest, boolean includeStackTrace) {
        Throwable error = this.getError(webRequest);
        if (error != null) {
            while(true) {
                if (!(error instanceof ServletException) || error.getCause() == null) {
                    this.addErrorMessage(errorAttributes, error);
                    break;
                }

                error = error.getCause();
            }
        }

        Object message = this.getAttribute(webRequest, "jakarta.servlet.error.message");
        if ((!StringUtils.isEmpty(message) || errorAttributes.get("message") == null) && !(error instanceof BindingResult)) {
            errorAttributes.put("msg", StringUtils.isEmpty(message) ? "No message available" : message);
        }

    }

    private void addErrorMessage(Map<String, Object> errorAttributes, Throwable error) {
        BindingResult result = this.extractBindingResult(error);
        if (result == null) {
            errorAttributes.put("msg", error.getMessage());
        } else {
            if (result.hasErrors()) {
                errorAttributes.put("msg", "Validation failed for object='" + result.getObjectName() + "'. Error count: " + result.getErrorCount());
            } else {
                errorAttributes.put("msg", "No errors");
            }

        }
    }

    private BindingResult extractBindingResult(Throwable error) {
        if (error instanceof BindingResult) {
            return (BindingResult)error;
        } else {
            return error instanceof MethodArgumentNotValidException ? ((MethodArgumentNotValidException)error).getBindingResult() : null;
        }
    }



}
