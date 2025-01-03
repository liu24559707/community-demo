package com.liu.controller;

import com.liu.entity.Entrust;
import com.liu.service.AcceptService;
import com.liu.service.EntrustService;
import com.liu.utils.Result;
import com.liu.utils.UserHolder;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("entrust")
public class EntrustController {

    @Resource
    private EntrustService entrustService;

    @Resource
    private AcceptService acceptService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    /**
     * 查看委托
     */
    @GetMapping("query")
    public Result queryEntrust(@RequestParam Integer selected) {
        if (selected == 0) { //查看所有委托
            return entrustService.queryEntrust();
        }
        if (selected == 1) { //查看已接受的委托
            return acceptService.queryAccept();
        }
        if (selected == 2) { //查看已发布的委托
            return entrustService.queryPublishEntrusr();
        }
        return Result.build(null,500,"请求错误,请重新检查请求");
    }

    /**
     * 刷新委托
     */
    @GetMapping("refresh/{selected}")
    public Result refreshEntrust(@PathVariable Integer selected) {
        if (selected == 0) {
            redisTemplate.delete("entrust:all");
        }
        if (selected == 1) {
            String userId = UserHolder.getUserId();
            redisTemplate.delete("accept:"+userId);
        }
        if (selected == 2) {
            String userId = UserHolder.getUserId();
            redisTemplate.delete("entrust:publish:"+userId);
        }
        return queryEntrust(selected);
    }

    /**
     * 发布委托
     */
    @PostMapping("publisher")
    public Result publishEntrust(@RequestBody Entrust entrust) {
        return entrustService.publisher(entrust);
    }

    /**
     * 接收委托
     */
    @PostMapping("accept/{id}")
    public Result acceptEntrust(@PathVariable Long id) {
        return acceptService.accept(id);
    }

    /**
     * 撤销委托
     */
    @DeleteMapping("del/{id}")
    public Result delEntrust(@PathVariable Long id) {
        return entrustService.del(id);
    }

    /**
     * 查看紧急委托
     */
}
