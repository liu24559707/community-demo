package com.liu.service;

import com.liu.entity.Entrust;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.utils.Result;

/**
* @author 86151
* @description 针对表【entrust】的数据库操作Service
* @createDate 2024-12-31 09:25:38
*/
public interface EntrustService extends IService<Entrust> {

    Result queryEntrust();

    Result publisher(Entrust entrust);

    Result queryPublishEntrusr();

    Result del(Long id);
}
