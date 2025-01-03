package com.liu.service;

import com.liu.entity.Paymentrecords;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.utils.Result;

/**
* @author 86151
* @description 针对表【paymentrecords】的数据库操作Service
* @createDate 2024-12-27 09:38:36
*/
public interface PaymentrecordsService extends IService<Paymentrecords> {

    Result queryAll(Long userId);
}
