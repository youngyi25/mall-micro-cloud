package com.example.mall.util;

import org.springframework.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;

public class ClientUtil {
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if(!StringUtils.hasText(ip)||"unkown".equalsIgnoreCase(ip)){
            ip = request.getHeader("X-Forwarded-For");
        }
        if(!StringUtils.hasText(ip)||"unkown".equalsIgnoreCase(ip)){
            ip = request.getRemoteAddr();
        }
        if(ip!=null&&ip.contains(",")){
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
    /**
     * 解析浏览器+操作系统（简易版，可按需增强）
     */
    public static String getClientInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (!StringUtils.hasText(userAgent)) {
            return "未知客户端";
        }
        String browser = "未知浏览器";
        String os = "未知系统";

        if (userAgent.contains("Chrome")) {
            browser = "Chrome";
        } else if (userAgent.contains("Firefox")) {
            browser = "Firefox";
        } else if (userAgent.contains("Edg")) {
            browser = "Edge";
        }

        if (userAgent.contains("Windows")) {
            os = "Windows";
        } else if (userAgent.contains("Mac")) {
            os = "MacOS";
        } else if (userAgent.contains("Android")) {
            os = "Android";
        } else if (userAgent.contains("iPhone")) {
            os = "iOS";
        }
        return browser + " | " + os;
    }
}