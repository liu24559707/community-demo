package com.liu.service;

import com.liu.entity.Newstype;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.utils.Result;

/**
* @author 86151
* @description 针对表【newstype】的数据库操作Service
* @createDate 2024-12-23 08:44:19
*/
public interface NewstypeService extends IService<Newstype> {

    Result listType();
}
