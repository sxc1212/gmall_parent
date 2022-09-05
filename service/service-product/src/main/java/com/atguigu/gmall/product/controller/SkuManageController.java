package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * author:atGuiGu-mqx
 * date:2022/8/30 10:18
 * 描述：
 **/
@RestController
@RequestMapping("admin/product")
public class SkuManageController {

    @Autowired
    private ManageService manageService;

    //  http://localhost/admin/product/saveSkuInfo
    @PostMapping("saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
        //  调用服务层方法
        this.manageService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    //  Request URL: http://localhost/admin/product/list/1/10?category3Id=61
    //  根据三级分类Id 查询skuInfo 列表
    @GetMapping("/list/{page}/{limit}")
    public Result getSkuInfoList(@PathVariable Long page,
                                 @PathVariable Long limit,
                                 SkuInfo skuInfo){
        //  创建一个分页对象
        Page<SkuInfo> skuInfoPage = new Page<>(page, limit);
        //  调用服务层方法
        IPage iPage = this.manageService.getSkuInfoList(skuInfoPage,skuInfo);
        //  返回数据
        return Result.ok(iPage);
    }

    //  http://localhost/admin/product/onSale/28
    //  上架：is_sale = 1
    @GetMapping("onSale/{skuId}")
    public Result onSale(@PathVariable Long skuId){
        //  调用服务层方法
        this.manageService.onSale(skuId);
        return Result.ok();
    }
    //  http://localhost/admin/product/cancelSale/22
    //  上架：is_sale = 0
    @GetMapping("cancelSale/{skuId}")
    public Result cancelSale(@PathVariable Long skuId){
        //  调用服务层方法
        this.manageService.cancelSale(skuId);
        return Result.ok();
    }

}