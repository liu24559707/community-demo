package com.liu.service;

import com.liu.entity.Accept;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.utils.Result;

/**
* @author 86151
* @description 针对表【accept】的数据库操作Service
* @createDate 2024-12-31 09:25:38
*/
public interface AcceptService extends IService<Accept> {

    Result queryAccept();

    Result accept(Long id);
}
