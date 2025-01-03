package com.liu.mapper;

import com.liu.entity.News;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 86151
* @description 针对表【news】的数据库操作Mapper
* @createDate 2024-12-21 19:36:36
* @Entity com.atguigu.cloud.entity.News
*/
public interface NewsMapper extends BaseMapper<News> {


    List<Long> selectIds(String search, int typeId, int start, int pageSize);
}




