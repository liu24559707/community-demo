package com.liu.service;

import com.liu.entity.Paymentdemo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.entity.Properties;
import com.liu.utils.Result;

import java.util.List;

/**
* @author 86151
* @description 针对表【paymentdemo】的数据库操作Service
* @createDate 2024-12-27 09:38:36
*/
public interface PaymentdemoService extends IService<Paymentdemo> {

    Result createDemo(Paymentdemo paymentdemo);

    void insertRecords(List<Properties> properties, Paymentdemo paymentdemo);
}
