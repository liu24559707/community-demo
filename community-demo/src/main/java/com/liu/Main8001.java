package com.liu;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.liu.ws.ChatEndPoint;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@EnableTransactionManagement
@MapperScan("com.liu.mapper")
@SpringBootApplication
@EnableWebSocket
//当前的代理对象暴露给 AOP 上下文中的其他组件。
// 这意味着你可以在任何时候通过 AopContext.currentProxy() 方法获取到当前正在执行的代理对象。
//需要添加AspectJ依赖
@EnableAspectJAutoProxy(exposeProxy = true)
public class Main8001 {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(Main8001.class, args);
    }

    //添加mybatisPlus的插件
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());//分页插件
        return interceptor;
    }
}