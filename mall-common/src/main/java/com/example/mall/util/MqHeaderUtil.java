package com.example.mall.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
public class MqHeaderUtil {

    /**
     * 提取当前Web线程所有请求头（token、租户、认证信息）
     */
    public static Map<String, String> extractHttpHeaders() {
        Map<String, String> headerMap = new HashMap<>();
        RequestAttributes attr = RequestContextHolder.getRequestAttributes();
        if (!(attr instanceof ServletRequestAttributes)) {
            return headerMap;
        }
        HttpServletRequest request = ((ServletRequestAttributes) attr).getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerKey = headerNames.nextElement();
            headerMap.put(headerKey, request.getHeader(headerKey));
        }

        return headerMap;
    }
    /**
     * 创建消息头，不是手动传递，无法从消息中获取
     */
    public static void buildMockRequestContext() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        RequestAttributes attributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    /**
     * 从消息头恢复Web上下文，绑定到当前消费线程
     */
    public static void buildMockRequestContext(Map<String, Object> msgHeaders) {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        msgHeaders.forEach((k, v) -> mockRequest.addHeader(k, String.valueOf(v)));
        ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    /**
     * 清理ThreadLocal，防止线程池复用串数据
     */
    public static void clearContext() {
        RequestContextHolder.resetRequestAttributes();
    }
}
