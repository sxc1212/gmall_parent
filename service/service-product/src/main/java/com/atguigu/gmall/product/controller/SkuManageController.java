package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("admin/product")
public class SkuManageController {

    @Autowired
    private ManageService manageService;


    @PostMapping("saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){

        this.manageService.saveSkuInfo(skuInfo);
        return Result.ok();
    }



    @GetMapping("/list/{page}/{limit}")
    public Result getSkuInfoList(@PathVariable Long page,
                                 @PathVariable Long limit,
                                 SkuInfo skuInfo){

        Page<SkuInfo> skuInfoPage = new Page<>(page, limit);

        IPage iPage = this.manageService.getSkuInfoList(skuInfoPage,skuInfo);

        return Result.ok(iPage);
    }



    @GetMapping("onSale/{skuId}")
    public Result onSale(@PathVariable Long skuId){

        this.manageService.onSale(skuId);
        return Result.ok();
    }


    @GetMapping("cancelSale/{skuId}")
    public Result cancelSale(@PathVariable Long skuId){

        this.manageService.cancelSale(skuId);
        return Result.ok();
    }

}