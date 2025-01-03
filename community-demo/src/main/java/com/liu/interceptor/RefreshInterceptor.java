package com.liu.interceptor;

import com.liu.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

/**
 * 刷新登录寿命，确保两小时之内无请求，则说明登录超时
 */
public class RefreshInterceptor implements HandlerInterceptor {

    private RedisTemplate<String, Object> redisTemplate;

    public RefreshInterceptor(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = UserHolder.getUserId();
        String key = "user:" + userId;
        if (userId != null) {
            redisTemplate.expire(key,2, TimeUnit.HOURS);
        }
        return true;
    }
}
