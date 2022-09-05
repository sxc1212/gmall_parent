package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.TestService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * author:atGuiGu-mqx
 * date:2022/9/2 10:30
 * 描述：
 **/
@Api(tags = "测试接口")
@RestController
@RequestMapping("admin/product/test")
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping("testLock")
    public Result testLock(){
        //  调用方法
        testService.testLock();
        return Result.ok();
    }

    @GetMapping("read")
    public Result readLock(){
        //  调用方法
        String msg = testService.readLock();
        return Result.ok(msg);
    }

    @GetMapping("write")
    public Result writeLock(){
        //  调用方法
        String msg = testService.writeLock();
        return Result.ok(msg);
    }
}