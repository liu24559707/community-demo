package com.liu.interceptor;

import com.alibaba.fastjson2.JSON;
import com.liu.entity.Users;
import com.liu.utils.JwtHelper;
import com.liu.utils.Result;
import com.liu.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

public class LoginInterceptor implements HandlerInterceptor {

    private RedisTemplate<String,Object> redisTemplate;

    private JwtHelper jwtHelper;

    //利用构造器获取redisTemplate
    public LoginInterceptor(RedisTemplate<String, Object> redisTemplate,JwtHelper jwtHelper) {
        this.redisTemplate = redisTemplate;
        this.jwtHelper = jwtHelper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Token");
        Long userId = jwtHelper.getUserId(token);
        Map<Object, Object> entries = redisTemplate.opsForHash().entries("user:" + userId);
        //登录超时
            response.setContentType("application/json;charset=utf-8");
        if(entries.isEmpty()||jwtHelper.isExpiration(token)){
            response.getWriter().write(
                    JSON.toJSONString(Result.build(null,510,"登录超时")));
            return false;
        }
        //转化为对象
        Users user = JSON.parseObject(JSON.toJSONString(entries), Users.class);
        //判断是否时同一用户登录
        if(!user.getToken().equals(token)){//异地登录
           response.getWriter().write(
                   JSON.toJSONString(Result.build(null,555,"账号异地登录"))
           );
           return false;
        }
        //存放userId,供之后请求调用
        UserHolder.setUserId(userId+"");
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUserId();
    }
}
