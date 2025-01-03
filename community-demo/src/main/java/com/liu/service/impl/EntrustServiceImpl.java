package com.liu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.entity.Accept;
import com.liu.entity.Entrust;
import com.liu.mapper.AcceptMapper;
import com.liu.service.EntrustService;
import com.liu.mapper.EntrustMapper;
import com.liu.utils.IdAndTimeGeneration;
import com.liu.utils.Result;
import com.liu.utils.UserHolder;
import com.liu.utils.dataType;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
* @author 86151
* @description 针对表【entrust】的数据库操作Service实现
* @createDate 2024-12-31 09:25:38
*/
@Service
public class EntrustServiceImpl extends ServiceImpl<EntrustMapper, Entrust>
    implements EntrustService{


    @Resource
    private EntrustMapper entrustMapper;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private IdAndTimeGeneration idAndTimeGeneration;

    @Resource
    private AcceptMapper acceptMapper;

    /**
     * 查询所有的委托
     * @return
     */
    @Override
    public Result queryEntrust() {
        String key = "entrust:all";
        List<Object> range = redisTemplate.opsForList().range(key, 0, -1);

        if(range != null&&!range.isEmpty()){
            return Result.ok(range);
        }
        LambdaQueryWrapper<Entrust> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Entrust::getGrade);
        queryWrapper.orderByDesc(Entrust::getCreateTime);
        List<Entrust> entrusts = entrustMapper.selectList(queryWrapper);
        Iterator<Entrust> iterator = entrusts.iterator();
        while(iterator.hasNext()){
            Entrust entrust = iterator.next();
            //排除超时的委托
            if(entrust.getEndTime().before(new Date())){
                iterator.remove();
            }else {
                redisTemplate.opsForList().rightPush(key,entrust);
            }
        }

        redisTemplate.expire(key,10, TimeUnit.MINUTES);
        return Result.ok(entrusts);
    }

    /**
     * 发布委托
     * @return
     */
    @Transactional
    @Override
    public Result publisher(Entrust entrust) {
        String key = "entrust:all";
        String userId = UserHolder.getUserId();
        long generate = idAndTimeGeneration.generate(dataType.Entrust);
        entrust.setEntrustid(generate);
        entrust.setPublisher(Long.valueOf(userId));
        Date now = new Date();
        Calendar instance = Calendar.getInstance();
        instance.setTime(now);
        instance.add(Calendar.DAY_OF_MONTH, 2);
        Date time = instance.getTime();
        entrust.setEndTime(time);
        int insert = entrustMapper.insert(entrust);
        if (insert > 0) {
         redisTemplate.delete(key);
         key = "entrust:publish:"+userId;
         redisTemplate.delete(key);
         return Result.ok(null);
        }
        return Result.build(null,500,"发布失败");
    }

    /**
     * 查看已发布的委托
     * @return
     */
    @Override
    public Result queryPublishEntrusr() {
        String userId = UserHolder.getUserId();
        String key = "entrust:publish:"+userId;
        List<Object> range = redisTemplate.opsForList().range(key, 0, -1);
        if(range != null&&!range.isEmpty()){
            return Result.ok(range);
        }
        LambdaQueryWrapper<Entrust> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Entrust::getCreateTime);
        queryWrapper.eq(Entrust::getPublisher, Long.valueOf(userId));
        List<Entrust> entrusts = entrustMapper.selectList(queryWrapper);
        entrusts.forEach(entrust -> {
            redisTemplate.opsForList().rightPush(key,entrust);
        });
        redisTemplate.expire(key,10, TimeUnit.MINUTES);
        return Result.ok(entrusts);
    }

    /**
     * 撤销委托
     * @param id
     * @return
     */
    @Transactional
    @Override
    public Result del(Long id) {
        String userId = UserHolder.getUserId();
        LambdaQueryWrapper<Entrust> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Entrust::getEntrustid, id);
        queryWrapper.eq(Entrust::getPublisher, Long.valueOf(userId));
        int rows = entrustMapper.delete(queryWrapper);
        if (rows > 0) {
            LambdaUpdateWrapper<Accept> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Accept::getEntrustid,id).setSql("progress = 2");
          acceptMapper.update(null, updateWrapper);
        String key = "entrust:all";
        redisTemplate.delete(key);
        String acKey = "accept:"+userId;
        redisTemplate.delete( acKey);
        key = "entrust:publish:"+userId;
        redisTemplate.delete(key);
        return Result.ok(null);
        }
        return Result.build(null,500,"撤销失败");
    }
}




