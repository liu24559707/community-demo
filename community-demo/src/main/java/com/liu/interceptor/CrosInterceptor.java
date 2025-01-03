package com.liu.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 跨域拦截器
 */

public class CrosInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE,PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,token,Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        //预检请求不放行
        if (request.getMethod().equals("OPTIONS")) {
            //204无内容
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return false;
        }
        return true;
    }
}
