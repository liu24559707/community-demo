package com.liu.config;

import com.liu.interceptor.CrosInterceptor;
import com.liu.interceptor.LoginInterceptor;
import com.liu.interceptor.RefreshInterceptor;
import com.liu.utils.JwtHelper;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private JwtHelper jwtHelper;

    /**
     * 拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CrosInterceptor()).addPathPatterns("/**").order(-1);
        registry.addInterceptor(new LoginInterceptor(redisTemplate,jwtHelper))
                .excludePathPatterns("/user/login","/user/getCode/{phone}","/user/register",
                        "/news/listAll","/news/queryDetail/{nid}","/news/listType").order(5);
        registry.addInterceptor(new RefreshInterceptor(redisTemplate)).order(10);
    }


}
