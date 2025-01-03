package com.liu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.entity.Paymentdemo;
import com.liu.entity.Paymentrecords;
import com.liu.entity.Properties;
import com.liu.mapper.PaymentrecordsMapper;
import com.liu.mapper.PropertiesMapper;
import com.liu.service.PaymentdemoService;
import com.liu.mapper.PaymentdemoMapper;
import com.liu.service.PaymentrecordsService;
import com.liu.utils.IdAndTimeGeneration;
import com.liu.utils.Result;
import com.liu.utils.dataType;
import jakarta.annotation.Resource;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
* @author 86151
* @description 针对表【paymentdemo】的数据库操作Service实现
* @createDate 2024-12-27 09:38:36
*/
@Service
public class PaymentdemoServiceImpl extends ServiceImpl<PaymentdemoMapper, Paymentdemo>
    implements PaymentdemoService{

    @Resource
    private PaymentdemoMapper paymentdemoMapper;

    @Resource
    private PaymentrecordsMapper paymentrecordsMapper;

    @Resource
    private PropertiesMapper propertiesMapper;

    @Resource
    private IdAndTimeGeneration idAndTimeGeneration;

    @Resource
    private PaymentrecordsService paymentRecordsService;

    /**
     * 创建一个缴费项目，同时给所有的房产创建缴费记录，注意提高效率
     * @param paymentdemo
     * @return
     */
    @Transactional
    @Override
    public Result createDemo(Paymentdemo paymentdemo) {
        long id = idAndTimeGeneration.generate(dataType.PaymentRecords);
        paymentdemo.setPaydid(id);
        //1.插入缴费项目
        paymentdemoMapper.insert(paymentdemo);
        //2.给所有房产生成缴费记录
        //2.1 查询所有的房产以及拥有者，按拥有者id排序
        List<Properties> properties = propertiesMapper.selectListWithPropIdAndOwnerId();
        //2.2 给所有的房产创建一个未缴费缴费记录
        //为了防止this调用导致事务失效
        PaymentdemoService paymentdemoService = (PaymentdemoService)AopContext.currentProxy();
        paymentdemoService.insertRecords(properties,paymentdemo);
        return Result.ok(null);
    }

    /**
     * 批量插入创建项目的缴费信息
     * 如果你是放入另一个类型则需要加上事务注解
     */
    public void insertRecords(List<Properties> properties, Paymentdemo paymentdemo) {
       List<Paymentrecords> paymentrecordsList = new ArrayList<>();
        properties.forEach(prop->{
            //因为不能存放存同一个引用
            Paymentrecords paymentrecords = new Paymentrecords();
            //批量转化为消费记录信息
            paymentrecords.setPayrid(idAndTimeGeneration.generate(dataType.PaymentRecords));
            paymentrecords.setPaydid(paymentdemo.getPaydid());
            paymentrecords.setPropid(prop.getPropid());
            paymentrecords.setUserId(prop.getOwnerid());
            paymentrecords.setProgress(0);
            paymentrecordsList.add(paymentrecords);
       });
        //将消费记录按10条分，
        // 思考如何实现多线程分批次插入，考虑多线程的事务一致性，
        // 考虑数据库连接池，防止因为连续点击导致多线程争夺数据库连接池导致影响其他项目功能
        //目前能力有限，但之后一定要实现
        List<List<Paymentrecords>> lists = ListUtil.partition(paymentrecordsList, 10);
        lists.forEach(list->{
            paymentRecordsService.saveBatch(list);
        });
    }
}




