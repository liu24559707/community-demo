package com.liu.controller;

import com.liu.entity.Complain;
import com.liu.service.ComplainService;
import com.liu.utils.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("complain")
public class ComplainController {
    @Resource
    private ComplainService complainService;

    /**
     * 查询自己的投诉
     */
    @GetMapping("find")
    public Result find() {
        return complainService.find();
    }

    /**
     * 上传自己的投诉
     */
    @PostMapping("publishComplain")
    public Result publishComplain(@RequestBody Complain complain) {
        return complainService.publish(complain);
    }
}
