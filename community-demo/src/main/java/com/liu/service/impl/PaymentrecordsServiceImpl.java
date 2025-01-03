package com.liu.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.util.BeanUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.liu.entity.News;
import com.liu.entity.Paymentrecords;
import com.liu.mapper.PaymentdemoMapper;
import com.liu.service.PaymentrecordsService;
import com.liu.mapper.PaymentrecordsMapper;
import com.liu.utils.Result;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
* @author 86151
* @description 针对表【paymentrecords】的数据库操作Service实现
* @createDate 2024-12-27 09:38:36
*/
@Service
public class PaymentrecordsServiceImpl extends ServiceImpl<PaymentrecordsMapper, Paymentrecords>
    implements PaymentrecordsService{



    @Resource
    private PaymentdemoMapper paymentDemoMapper;

    @Resource
    private PaymentrecordsMapper paymentRecordsMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 用户查询缴费记录(缴费记录分为已缴费和未缴费)
     * 缓存存储格式未 key = paymentRecords:用户id hashkey=消费记录id value=整条记录
     * @return
     */
    @Transactional
    @Override
    public Result queryAll(Long userId) {
      String key = "paymentrecords:" + userId;
      //1.获取缴费记录
      List<Long> ids = paymentRecordsMapper.queryIds(userId);
      //2.查询redis获取缴费记录id
         Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
      //3.两次id作比较，查看二者是否一致 ,二者一致，直接返回缓存，二者不一致，考虑数据库和缓存
        //3.1 id有剩余，说明缓存未命中这些数据，重建缓存，
        //3.2 entries有剩余则说明数据库不存在这些数据，删除这部分缓存
        if (!entries.isEmpty()) {
            List<Long> cacheIds = new ArrayList<>();
            entries.values().forEach(v->{
                cacheIds.add(((Paymentrecords)v).getPayrid());
            });
            List<Long> ids2 = new ArrayList<>(ids);
            //缓存中未存在的数据
            ids.removeAll(cacheIds);
            //数据库中未存在的数据,要删除
            cacheIds.removeAll(ids2);
            if (cacheIds.isEmpty()&&ids.isEmpty()) {//二者都为空，则说明二者数据一致
                List<Paymentrecords> paymentrecordsList = new ArrayList<>();
                entries.values().forEach(v->{
                    paymentrecordsList.add((Paymentrecords)v);
                });
                paymentrecordsList.sort(new Comparator<Paymentrecords>() {
                    @Override
                    public int compare(Paymentrecords o1, Paymentrecords o2) {
                        return o1.getProgress().compareTo(o2.getProgress());
                    }
                });
                return Result.ok(paymentrecordsList);
            }
            if (!cacheIds.isEmpty()) {
                cacheIds.forEach(id->{
                    redisTemplate.opsForHash().delete(key,id+"");
                });
            }
        }
        //4.查询未被命中的id，重建缓存
        if (!ids.isEmpty()) {
        List<Paymentrecords> paymentrecords = paymentRecordsMapper.queryWithRecordsAndDemo(ids,userId);
        paymentrecords.forEach(r->{
            redisTemplate.opsForHash().put(key,r.getPayrid()+"",r);
        });
        redisTemplate.expire(key,10, TimeUnit.MINUTES);
        }
        //5.查询最新缓存
        Map<Object, Object> entries1 = redisTemplate.opsForHash().entries(key);
        List<Paymentrecords> paymentrecords1 = new ArrayList<>();
        entries1.values().forEach(v->{
            paymentrecords1.add((Paymentrecords)v);
        });
        paymentrecords1.sort(new Comparator<Paymentrecords>() {
            @Override
            public int compare(Paymentrecords o1, Paymentrecords o2) {
                return o1.getProgress().compareTo(o2.getProgress());
            }
        });
        return Result.ok(paymentrecords1);
    }
}




