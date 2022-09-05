package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("admin/product/")

public class ManageController {

    @Autowired
    private ManageService manageService;


    @GetMapping("getCategory1")
    public Result getCategory1(){

        List<BaseCategory1> baseCategory1List = manageService.getCategory1();

        return Result.ok(baseCategory1List);
    }




    @GetMapping("getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable Long category1Id){

        List<BaseCategory2> baseCategory2List = this.manageService.getCategory2(category1Id);
        return Result.ok(baseCategory2List);
    }


    @GetMapping("getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable Long category2Id){

        List<BaseCategory3> baseCategory3List = this.manageService.getCategory3(category2Id);
        return Result.ok(baseCategory3List);
    }


    @GetMapping("attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result getAttrInfoList(@PathVariable Long category1Id,
                                  @PathVariable Long category2Id,
                                  @PathVariable Long category3Id
                                  ){

        List<BaseAttrInfo> baseAttrInfoList = this.manageService.getAttrInfoList(category1Id,category2Id,category3Id);

        return Result.ok(baseAttrInfoList);

    }


    
    @PostMapping("saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){

        this.manageService.saveAttrInfo(baseAttrInfo);

        return Result.ok();
    }




    @GetMapping("getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable Long attrId){


        BaseAttrInfo baseAttrInfo = this.manageService.getAttrInfo(attrId);




        return Result.ok(baseAttrInfo.getAttrValueList());

    }
}