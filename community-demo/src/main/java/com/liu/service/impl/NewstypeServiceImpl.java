package com.liu.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.entity.Newstype;
import com.liu.service.NewstypeService;
import com.liu.mapper.NewstypeMapper;
import com.liu.utils.CacheClient;
import com.liu.utils.Result;
import com.liu.utils.dataType;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
* @author 86151
* @description 针对表【newstype】的数据库操作Service实现
* @createDate 2024-12-23 08:44:19
*/
@Service
public class NewstypeServiceImpl extends ServiceImpl<NewstypeMapper, Newstype>
    implements NewstypeService{



    @Resource
    private CacheClient cacheClient;

    /**
     * 查询所有的新闻类型
     * 按编号大小来排序
     * @return
     */
    @Override
    public Result listType() {
        //查看redis是否命中
        String s = cacheClient.get(dataType.NewsTypeData.getDataTypeId()+"");
        if (StringUtils.isNotBlank(s)) {//字符串非空字符串，命中
            List<Newstype> newstypes = JSON.parseArray(s, Newstype.class);
            return Result.ok(newstypes);
        }

        List<Newstype> list = this.list();
        //按编号大小来排序
        list.sort(new Comparator<Newstype>() {
            @Override
            public int compare(Newstype o1, Newstype o2) {
                return o1.getTypeid()-o2.getTypeid();
            }
        });
        //如果为空
        String jsonString = null;
        if(!list.isEmpty()){
            jsonString = JSON.toJSONString(list);
        }
        cacheClient.set(dataType.NewsTypeData.getDataTypeId()+"", jsonString,3,2,TimeUnit.MINUTES);
        return Result.ok(list);
    }
}




