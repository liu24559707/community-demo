package com.liu.controller;

import com.liu.entity.Paymentdemo;
import com.liu.service.PaymentdemoService;
import com.liu.service.PaymentrecordsService;
import com.liu.utils.Result;
import com.liu.utils.UserHolder;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("payment")
public class PaymentController {

    @Resource
    private PaymentdemoService paymentDemoService;

    @Resource
    private PaymentrecordsService paymentRecordsService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;



    /**
     * 管理员查看缴费项目
     * @return
     */
    @GetMapping("getDemo")
    public Result getDemo() {
        List<Paymentdemo> list = paymentDemoService.list();
        return Result.ok(list);
    }


    /**
     * 管理员创建缴费项目
     */
    @PostMapping("saveDemo")
    public Result save(@RequestBody Paymentdemo paymentdemo) {
        return paymentDemoService.createDemo(paymentdemo);
    }

    /**
     * 用户查看缴费记录
     */
    @GetMapping("query")
    public Result query() {
        String userId = UserHolder.getUserId();
        return paymentRecordsService.queryAll(Long.valueOf(userId));
    }


}
