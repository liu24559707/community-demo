package com.liu.utils;


import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public class RedisLock {

    private RedisTemplate<String, Object> redisTemplate;

    //业务名
    private String name;

    //用户编号
    private String username;



    public RedisLock(RedisTemplate<String, Object> redisTemplate, String name, String username) {
        this.redisTemplate = redisTemplate;
        this.name = name;
        this.username = username;
    }


    //按秒锁
    public boolean lockSeconds(int seconds) {
        String key = "lock:"+ name+":"+username;
        Boolean b = redisTemplate.opsForValue().setIfAbsent(key, username, seconds, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(b);
    }

    //释放锁
    public void unlock() {
        String key = "lock:"+ name+":"+username;
         redisTemplate.delete(key);
    }
}
