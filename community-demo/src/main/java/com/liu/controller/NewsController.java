package com.liu.controller;

import cn.hutool.core.util.NumberUtil;
import com.liu.entity.News;
import com.liu.service.NewsService;
import com.liu.service.NewstypeService;
import com.liu.utils.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 这一部分不需要用户登录
 */

@RestController
@RequestMapping("news")
public class NewsController {

    @Resource
    private NewsService newsService;

    @Resource
    private NewstypeService newstypeService;

    /**
     * 分页带条件所有的新闻的标题
     * search:查询条件，与title相关
     * type:类型
     * page：页码
     * pageSize：单页大小
     */
    @GetMapping("listAll")
    public Result list(@RequestParam String search,@RequestParam Integer typeId, @RequestParam Integer page, @RequestParam Integer pageSize) {
        return newsService.listAll(search,typeId,page,pageSize);
    }

    /**
     * 根据新闻id查看新闻详情
     */
    @GetMapping("queryDetail/{nid}")
    public Result queryDetail(@PathVariable Long nid) {
        return newsService.queryDetail(nid);
    }

    /**
     * 发布新闻
     * 发布新闻只能是社区的管理者
     */
    @PostMapping("add")
    public Result add(@RequestBody News news) {
        return newsService.add(news);
    }

    /**
     * 删除新闻
     */
    @DeleteMapping("del/{id}")
    public Result del(@PathVariable Long id) {
        return newsService.del(id);
    }

    /**
     * 查看所有新闻类型
     */
    @GetMapping("listType")
    public Result listType() {
        return newstypeService.listType();
    }



}
