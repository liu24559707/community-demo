package com.liu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.entity.Accept;
import com.liu.entity.Entrust;
import com.liu.mapper.EntrustMapper;
import com.liu.service.AcceptService;
import com.liu.mapper.AcceptMapper;
import com.liu.utils.IdAndTimeGeneration;
import com.liu.utils.Result;
import com.liu.utils.UserHolder;
import com.liu.utils.dataType;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
* @author 86151
* @description 针对表【accept】的数据库操作Service实现
* @createDate 2024-12-31 09:25:38
*/
@Service
public class AcceptServiceImpl extends ServiceImpl<AcceptMapper, Accept>
    implements AcceptService{

    @Resource
    private AcceptMapper acceptMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private IdAndTimeGeneration idAndTimeGeneration;

    @Resource
    private EntrustMapper entrustMapper;

    /**
     * 查看已接收的委托
     * @return
     */
    @Override
    public Result queryAccept() {
        String userId = UserHolder.getUserId();
        String key = "accept:"+userId;
        List<Object> range = redisTemplate.opsForList().range(key, 0, -1);
        if (range != null&&!range.isEmpty()) {
            return Result.ok(range);
        }
        //查询接收委托以及其内容,完成度按升序排，接受者id与用户id相等

        List<Accept> list = acceptMapper.selectAllList(userId);

       list.forEach(accept->{
           redisTemplate.opsForList().rightPush(key,accept);
       });
       redisTemplate.expire(key,10, TimeUnit.MINUTES);
        return Result.ok(list);
    }

    /**
     * 接收委托
     * @param id
     * @return
     */
    @Transactional
    @Override
    public Result accept(Long id) {
        String userId = UserHolder.getUserId();
        LambdaQueryWrapper<Entrust> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Entrust::getEntrustid,id);
        queryWrapper1.eq(Entrust::getPublisher,Long.valueOf(userId));
        Entrust entrust1 = entrustMapper.selectOne(queryWrapper1);
        if (entrust1!=null) {
            return Result.build(null,500,"不可以接收自己的委托");
        }
        //查看自己是否接收过该委托
        LambdaQueryWrapper<Accept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Accept::getAcceptUser,Long.parseLong(userId));
        queryWrapper.eq(Accept::getEntrustid,id);
        Accept accept1 = acceptMapper.selectOne(queryWrapper);
        if(accept1!=null){
            return Result.build(null,500,"已接收过该委托");
        }

        //查看委托是否超过结束时间
        if (redisTemplate.opsForHash().hasKey("OverTime:entrust",id+"")) {
            return Result.build(null,500,"委托已超时");
        }
        LambdaQueryWrapper<Entrust> entrustQueryWrapper = new LambdaQueryWrapper<>();
        entrustQueryWrapper.eq(Entrust::getEntrustid,id);
        entrustQueryWrapper.gt(Entrust::getEndTime,new Date());
        Entrust entrust = entrustMapper.selectOne(entrustQueryWrapper);
        if(entrust ==null){
            return Result.build(null,500,"委托已超时");
        }

        long generate = idAndTimeGeneration.generate(dataType.Accept);
        Accept accept = new Accept();
        accept.setAcceptid(generate);
        accept.setAcceptUser(Long.parseLong(userId));
        accept.setProgress(0);
        accept.setEntrustid(id);

        int insert = acceptMapper.insert(accept);

        if (insert > 0) {
        String key = "accept:"+userId ;
        redisTemplate.delete(key);
        redisTemplate.opsForHash().put("OverTime:entrust",entrust.getEntrustid()+"",
                entrust.getEndTime());
        redisTemplate.expire(key,10, TimeUnit.MINUTES);
        return Result.ok(accept);
        }
        return Result.build(null,500,"接受过程中出错");
    }
}




