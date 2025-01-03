package com.liu.service;

import com.liu.entity.Notice;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.utils.Result;

/**
* @author 86151
* @description 针对表【notice】的数据库操作Service
* @createDate 2024-12-31 08:14:30
*/
public interface NoticeService extends IService<Notice> {

    Result addNotice(Notice notice);

    Result queryAll();
}
