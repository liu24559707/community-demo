package com.liu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liu.service.NewsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.entity.News;
import com.liu.mapper.NewsMapper;
import com.liu.utils.CacheClient;
import com.liu.utils.IdAndTimeGeneration;
import com.liu.utils.Result;
import com.liu.utils.dataType;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
* @author 86151
* @description 针对表【news】的数据库操作Service实现
* @createDate 2024-12-21 19:36:36
*/
@Slf4j
@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News>
    implements NewsService {
    @Resource
    private NewsMapper newsMapper;

    @Resource
    private CacheClient cacheClient;

    @Resource
    private IdAndTimeGeneration idGeneration;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 分页带条件查询所有新闻标题
     * @return
     *
     *  redis思路
     *  redis分页模糊查询，存放数据类型为SortSet,按时间排序
     *  查询redis是否命中
     *  使用该类型我们只需要删除被操作的缓存，在查询时，再试着重建缓存
     *  存放位置basePath:(search+"-"+typeId)，部分数据可能相同，但键不一样，即查询条件不一样
     *  我们先查询需要被查的新闻id列表，之后再查询缓存已经存在的新闻id列表，
     *  两次列表进行比较，对未命中的新闻id进行重建缓存
     *  关键命令： zrangebyscore key min max：按照score排序后，获取指定范围内的的元素
     *  使用sortset进行排序管理，hash进行数据管理
     */
    @Transactional
    @Override
    public Result listAll(String search, Integer typeId,Integer page, Integer pageSize) {
        Page<News> page1 = new Page<>(page,pageSize);
        //1.规定键
        String basePath= dataType.NewsData.getDataTypeId()+":";
        String key = basePath+typeId+":"+"-"+search;


        //根据page和pageSize逆序查看缓存中的数据

        Set<String> range = cacheClient.getStringRedisTemplate().opsForZSet()
                .reverseRange(key, (page-1)*pageSize, page*pageSize-1);

        //2.检查是否命中，分两种情况
        //2.1 查看此次查询所有的要查看的id以及数据库del为0的总数
        List<Long> ids = newsMapper.selectIds(search,typeId,(page-1)*pageSize,pageSize);
        //数据库中没有这样的数据，直接返回一个null
        if (ids.isEmpty()) {
            return Result.ok(null);
        }
        page1.setTotal(this.count());

        //2.2.如果是set大小大于0，分两种情况，将查询id列表与查询缓存的id列表，做一个差，算出需要重建的id列表
        List <Long> needIds =new ArrayList<>();
        if (range.size()>0) {
            //2.2.1将range转化为一个List<News>集合,获取缓存id
            List<News> news = JSON.parseArray(range.toString(), News.class);
            List<Long> cacheIds = new ArrayList<>(news.stream().map(News::getNid).toList());

            //做一个差，获得差后的id列表,由于缓存更新策略
            List<Long> ids2 = new ArrayList<>(ids);
            boolean b = ids.removeAll(cacheIds);
            cacheIds.removeAll(ids2);

            //2.2.2 如果查询id列表与缓存id列表一致，直接返回缓存数据,由于二者查询的数量时相同的
            if ( ids.isEmpty()) {
                List<News> collect = ids2.stream().map(nid -> {
                    String key1 = "newsDetails:" + dataType.NewsData.getDataTypeId() + ":" + nid;
                    Map<Object, Object> entries = redisTemplate.opsForHash().entries(key1);
                    return JSON.parseObject(JSON.toJSONString(entries), News.class);
                }).collect(Collectors.toList());
                page1.setRecords(collect);
                return Result.ok(page1);
            }else {
                //2.2.3 如果查询id列表与缓存列表不一致，需要重建的id列表为二者之差，分为三种情况
                //2.2.3.1 ids为空，但cacheIds不为空或ids不为空,cacheIds为空或二者都不为空
                //这两种数据的空，代表行为，ids不为空，去插入缓存，cacheIds不为空，去删除缓存
                needIds.addAll(ids);
                //这一步要写，1.因为查询条件的不同，我们无法在删除数据库的同时，删除缓存，所以我们将删除操作放在查询中
                //2.删除缓存中逻辑不合理，但程序合理的数据，比如 数据库中只有 1，3 ，
                // 但缓存中有1，2，3，4，我们要查看前两个数时，正确为1，3，但我们从缓存中查看到 1 ，2
                if (!cacheIds.isEmpty()) {
                    cacheIds.forEach(id->{
                        cacheClient.getStringRedisTemplate().opsForZSet()
                                .removeRangeByScore(key,id,id);
                    });
                }
            }
        }else {
            //2.3 如果，需要重建的缓存id为该查询的全部
            needIds.addAll(ids);
        }

        //3 根据得到的重建id列表，从数据库查询并重建缓存
        if (!needIds.isEmpty()) {
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(News::getNid,needIds);
        List<News> news = newsMapper.selectList(queryWrapper);
        for (News news1 : news) {
            //sortaSet存储顺序
            cacheClient.getStringRedisTemplate().opsForZSet().add(key, JSON.toJSONString(news1)
                    ,news1.getNid());
            String key1 = "newsDetails:" + dataType.NewsData.getDataTypeId() + ":" + news1.getNid();
            Map<String, Object> stringObjectMap = BeanUtil.beanToMap(news1);
            //存放具体数据
            redisTemplate.opsForHash().putAll(key1,stringObjectMap);
            redisTemplate.expire(key1,10,TimeUnit.MINUTES);
        }
        cacheClient.getStringRedisTemplate().expire(key,10,TimeUnit.MINUTES);
        }

        //4 再次查询缓存，获取重建后的数据
        Set<String> range2 = cacheClient.getStringRedisTemplate().opsForZSet()
                .reverseRange(key, (page-1)*pageSize, page*pageSize-1);
        List<News> news1 = JSON.parseArray(range2.toString(), News.class);
        List<Long> ids2 = new ArrayList<>(news1.stream().map(News::getNid).toList());
        List<News> collect = ids2.stream().map(nid -> {
            String key1 = "newsDetails:" + dataType.NewsData.getDataTypeId() + ":" + nid;
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(key1);
            return JSON.parseObject(JSON.toJSONString(entries), News.class);
        }).toList();
        page1.setRecords(collect);
        return Result.ok(page1);
    }

    /**
     * 查看新闻详情
     * 查看新闻详情的同时点击量也会增加
     */
    @Override
    @Transactional
    public Result queryDetail(Long nid) {
        String key = "newsDetails:"+dataType.NewsData.getDataTypeId()+":"+nid;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        News news = null;
        //从缓存中查询数据，不存在则重建缓存，存在则转化为News,同时需要将缓存的点击量+1，返回给前端
        if (!entries.isEmpty()) {
            news = JSON.parseObject(JSON.toJSONString(entries), News.class);
            redisTemplate.opsForHash().put(key,"clickCount",news.getClickCount()+1);
            news.setClickCount(news.getClickCount()+1);
            return Result.ok(news);
        }

        LambdaUpdateWrapper<News> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(News::getNid,nid).setSql("clickCount = clickCount + 1");
        this.update(wrapper);

         news = this.getById(nid);
        if (news != null) {
            Map<String, Object> stringObjectMap = BeanUtil.beanToMap(news);
            System.out.println(stringObjectMap);
            redisTemplate.opsForHash().putAll(key, stringObjectMap);
        }
        return Result.ok(news);
    }

    /**
     * 增加新闻
     * @param news
     * @return
     */
    @Override
    public Result add(News news) {
        //生成并设置id
        idGeneration.generate(dataType.NewsData);
        return Result.ok(this.save(news));
    }

    /**
     * 删除新闻
     * 根据缓存更新策略，需要删除指定的数据
     * @param id
     * @return
     */
    @Transactional
    @Override
    public Result del(Long id) {
        boolean b = this.removeById(id);
        return Result.ok(b);
    }



}




