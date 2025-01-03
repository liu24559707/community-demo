package com.liu.service;

import com.liu.entity.News;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.utils.Result;

/**
* @author 86151
* @description 针对表【news】的数据库操作Service
* @createDate 2024-12-21 19:36:36
*/
public interface NewsService extends IService<News> {

    Result listAll(String search, Integer typeId,Integer page, Integer pageSize);

    Result add(News news);

    Result del(Long id);

    Result queryDetail(Long nid);
}
