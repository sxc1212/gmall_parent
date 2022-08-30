package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin/product/baseTrademark/")
public class BaseTrademarkController {

    @Autowired
    private BaseTrademarkService baseTrademarkService;


    @GetMapping("{page}/{limit}")
    public Result getTradeMarkList(@PathVariable Long page,
                                   @PathVariable Long limit
    ) {

        Page<BaseTrademark> baseTrademarkPage = new Page<>(page, limit);

        IPage iPage = this.baseTrademarkService.getTradeMarkList(baseTrademarkPage);

        return Result.ok(iPage);
    }


    @PostMapping("save")
    public Result saveTradeMark(@RequestBody BaseTrademark baseTrademark) {

        this.baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }


    @DeleteMapping("remove/{id}")
    public Result removeById(@PathVariable Long id) {

        this.baseTrademarkService.removeById(id);
        return Result.ok();
    }


    @GetMapping("get/{id}")
    public Result getById(@PathVariable Long id) {

        BaseTrademark baseTrademark = this.baseTrademarkService.getById(id);
        return Result.ok(baseTrademark);
    }


    @PutMapping("update")
    public Result update(@RequestBody BaseTrademark baseTrademark) {

        this.baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }


}