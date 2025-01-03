package com.liu.controller;

import com.liu.entity.Notice;
import com.liu.service.NoticeService;
import com.liu.utils.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("notice")
public class NoticeController {

    @Resource
    private NoticeService noticeService;

    /**
     * 管理员增加社区通知
     */
    @PostMapping("addNotice")
    public Result addNotice(@RequestBody Notice notice) {
        return noticeService.addNotice(notice);
    }


    /**
     * 查看所有的公告
     */
    @GetMapping("queryAll")
    public Result queryAll() {
        return noticeService.queryAll();
    }

}
