package com.atguigu.gmall.list.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.SearchService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("api/list")
public class ListApiController {

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    private SearchService searchService;

    @GetMapping("createIndex")
    public Result createIndex() {

        restTemplate.createIndex(Goods.class);
        restTemplate.putMapping(Goods.class);
        return Result.ok();
    }

    @GetMapping("inner/upperGoods/{skuId}")
    public Result upperGoods(@PathVariable Long skuId) {

        this.searchService.upperGoods(skuId);
        return Result.ok();
    }

    @GetMapping("inner/lowerGoods/{skuId}")
    public Result lowerGoods(@PathVariable Long skuId) {

        this.searchService.lowerGoods(skuId);
        return Result.ok();
    }


    @GetMapping("inner/incrHotScore/{skuId}")
    public Result incrHotScore(@PathVariable Long skuId) {

        this.searchService.incrHotScore(skuId);
        return Result.ok();
    }


    @PostMapping
    public Result search(@RequestBody SearchParam searchParam) {

        SearchResponseVo responseVo = null;
        try {
            responseVo = this.searchService.search(searchParam);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Result.ok(responseVo);
    }


}