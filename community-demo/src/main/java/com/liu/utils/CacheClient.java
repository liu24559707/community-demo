package com.liu.utils;

import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 使用redis充当缓存，并且解决缓存问题
 * 注意：缓存主动更新策略为查看数据时创建缓存，修改时删除缓存
 */
@Slf4j
@Component
public class CacheClient {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 存放数据并解决缓存穿透问题和缓存雪崩问题
     * 解决穿透问题：当看见数据库没有该数据时，创建一个空对象 ,
     * 解决雪崩问题：设置一个时间范围内的保证，大量数据不在同一时间失效
     * @param key 键
     * @param value 值,如果存放的是一个对象则需要先转化为json存放,如果是一个空对象直接传一个null
     * @param minTime 最小寿命
     * @param range  寿命范围
     * @param unit 时间单位
     * @Return boolean 是否插入
     * 设置的时间不能太长，因为为了保证数据的一致性，
     * 当redis之后存入了空对象，只要缓存未过期，请求获得的还是空对象，存在短期不一致性
     */
    public boolean set(String key, String value, int minTime, int range, TimeUnit unit) {
        //进行检查，判断该redis是否命中,如果命中则说明不需要插入
        if (stringRedisTemplate.hasKey(key)) {
            return false;
        }
        //redis未命中
        //设置随机时间
        minTime = RandomUtil.randomInt(minTime, minTime+range);
        stringRedisTemplate.opsForValue().setIfAbsent(key, value, (long) minTime,unit);
        //创建缓存成功
        return true;
    }

    /**
     * 解决热点key问题-缓存击穿-使用逻辑过期的思路
     * 解决缓存击穿：使用redis锁机制来解决，
     * 首先从redis获取数据，判断该数据是否逻辑过期，
     * 如果过期，则去争夺锁，只有一个请求才能获取锁，
     * 获取请求的锁开启一个子线程重建缓存，并且与未获取锁的请求继续获取旧数据
     * 注意当获取锁后需要二次判断是否过期，未过期，则不需要重建缓存
     */

    /**
     * 通过键值对获取对应数据或对象的json
     * @param key
     * @return
     */
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 创建一个无TTL限制的键值对
     * @param key
     * @param value
     * @return 是否创建成功
     */
    public boolean set(String key, String value) {
        if (stringRedisTemplate.hasKey(key)) {
            return false;
        }
        stringRedisTemplate.opsForValue().set(key, value);
        return true;
    }

    /**
     * 根据key删除键值对
     * @param key
     * @return
     */
    public boolean delete(String key) {
        if (stringRedisTemplate.hasKey(key)) {
            stringRedisTemplate.delete(key);
            return true;
        }
        return false;
    }

    /**
     * 获得stringRedisTemplate
     */
    public StringRedisTemplate getStringRedisTemplate() {
        return stringRedisTemplate;
    }
}
