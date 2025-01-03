package com.liu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.entity.Notice;
import com.liu.service.NoticeService;
import com.liu.mapper.NoticeMapper;
import com.liu.utils.IdAndTimeGeneration;
import com.liu.utils.Result;
import com.liu.utils.dataType;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
* @author 86151
* @description 针对表【notice】的数据库操作Service实现
* @createDate 2024-12-31 08:14:30
*/
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice>
    implements NoticeService{

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private IdAndTimeGeneration idAndTimeGeneration;

    @Resource
    private NoticeMapper noticeMapper;

    @Override
    public Result addNotice(Notice notice) {
        String key = "notice:all";
        long generate = idAndTimeGeneration.generate(dataType.Notice);
        notice.setNoticeid(generate);
        noticeMapper.insert(notice);
        redisTemplate.delete(key);
        return Result.ok(null);
    }

    @Override
    public Result queryAll() {
        String key = "notice:all";

        List<Object> range = redisTemplate.opsForList().range(key, 0, -1);
        if (range != null&&!range.isEmpty()) {
            return Result.ok(range);
        }
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Notice::getCreateTime);
        List<Notice> notices = noticeMapper.selectList(queryWrapper);
        if (notices!=null&&!notices.isEmpty()) {
            //redis序列化配置有问题所以只能一个一个插入,批量插入就会造成只插入一条数据
            notices.forEach(notice -> {
                redisTemplate.opsForList().rightPush(key, notice);
            });
              redisTemplate.expire(key,10, TimeUnit.MINUTES);
        }
        return Result.ok(notices);
    }


}




