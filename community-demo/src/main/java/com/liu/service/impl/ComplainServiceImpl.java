package com.liu.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.entity.Complain;
import com.liu.service.ComplainService;
import com.liu.mapper.ComplainMapper;
import com.liu.utils.IdAndTimeGeneration;
import com.liu.utils.Result;
import com.liu.utils.UserHolder;
import com.liu.utils.dataType;
import jakarta.annotation.Resource;
import org.json.JSONArray;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
* @author 86151
* @description 针对表【complain】的数据库操作Service实现
* @createDate 2025-01-10 15:29:44
*/
@Service
public class ComplainServiceImpl extends ServiceImpl<ComplainMapper, Complain>
    implements ComplainService{

    @Resource
    private ComplainMapper complainMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private IdAndTimeGeneration idAndTimeGeneration;

    /**
     * 查询用户的投诉
     * @return
     */
    @Override
    public Result find() {
        String userId = UserHolder.getUserId();
        List<Object> range = redisTemplate.opsForList().range("complain:" + userId, 0, -1);
        if (range != null&& !range.isEmpty()) {
           return Result.ok(range);
        }
        LambdaQueryWrapper<Complain> queryWrapper = new LambdaQueryWrapper<Complain>();
        queryWrapper.eq(Complain::getUser,Long.parseLong(userId));
        queryWrapper.orderByAsc(Complain::getProgress);
        queryWrapper.orderByDesc(Complain::getCreateTime);
        List<Complain> complains = this.list(queryWrapper);
        if(!complains.isEmpty()){
            complains.forEach(complain -> {
                redisTemplate.opsForList().rightPush("complain:" + userId,complain);
            });
            redisTemplate.expire("complain:"+userId,10, TimeUnit.MINUTES);
        }
        return Result.ok(complains);
    }

    /**
     * 发布自己的投诉
     * @param complain
     * @return
     */
    @Transactional
    @Override
    public Result publish(Complain complain) {
        String userId = UserHolder.getUserId();
        complain.setUser(Long.parseLong(userId));
        complain.setProgress(0);
        complain.setComplainid(idAndTimeGeneration.generate(dataType.Complain));
        int insert = complainMapper.insert(complain);
        if (insert>0) {
            redisTemplate.opsForList().rightPush("complain:"+userId,complain);
            redisTemplate.expire("complain:"+userId,10, TimeUnit.MINUTES);
            return Result.ok(null);
        }
        return Result.build(null,500,"发布投诉失败");
    }

}




