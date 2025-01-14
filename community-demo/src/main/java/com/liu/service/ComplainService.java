package com.liu.service;

import com.liu.entity.Complain;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.utils.Result;

/**
* @author 86151
* @description 针对表【complain】的数据库操作Service
* @createDate 2025-01-10 15:29:44
*/
public interface ComplainService extends IService<Complain> {

    Result find();

    Result publish(Complain complain);
}
