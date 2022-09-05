package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("admin/product/")
public class SpuManageController {

    @Autowired
    private ManageService manageService;







    
    @GetMapping("{page}/{limit}")
    public Result getSpuList(@PathVariable Long page,
                             @PathVariable Long limit,
                             SpuInfo spuInfo
                             ){


        Page<SpuInfo> spuInfoPage = new Page<>(page,limit);

        IPage<SpuInfo> infoIPage = this.manageService.getSpuList(spuInfoPage,spuInfo);

        return Result.ok(infoIPage);

    }


    @GetMapping("baseSaleAttrList")
    public Result getBaseSaleAttrList(){

        List<BaseSaleAttr> baseSaleAttrList = this.manageService.getBaseSaleAttrList();

        return Result.ok(baseSaleAttrList);
    }


    @PostMapping("saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){

        this.manageService.saveSpuInfo(spuInfo);

        return Result.ok();
    }


    @GetMapping("spuImageList/{spuId}")
    public Result getSpuImageList(@PathVariable Long spuId){

        List<SpuImage> spuImageList = this.manageService.getSpuImageList(spuId);
        return Result.ok(spuImageList);
    }



    @GetMapping("spuSaleAttrList/{spuId}")
    public Result getSpuSaleAttrList(@PathVariable Long spuId){

        List<SpuSaleAttr> spuSaleAttrList = this.manageService.getSpuSaleAttrList(spuId);
        return Result.ok(spuSaleAttrList);
    }


}