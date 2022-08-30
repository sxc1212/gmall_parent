package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.CategoryTrademarkVo;
import com.atguigu.gmall.product.service.BaseCategoryTrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/admin/product/baseCategoryTrademark/")
public class BaseCategoryTrademarkController {

    @Autowired
    private BaseCategoryTrademarkService baseCategoryTrademarkService;


    @GetMapping("findTrademarkList/{category3Id}")
    public Result findTrademarkList(@PathVariable Long category3Id) {

        List<BaseTrademark> baseTrademarkList = this.baseCategoryTrademarkService.findTrademarkList(category3Id);

        return Result.ok(baseTrademarkList);
    }


    @GetMapping("findCurrentTrademarkList/{category3Id}")
    public Result findCurrentTrademarkList(@PathVariable Long category3Id) {

        List<BaseTrademark> baseTrademarkList = this.baseCategoryTrademarkService.findCurrentTrademarkList(category3Id);

        return Result.ok(baseTrademarkList);
    }


    @PostMapping("save")
    public Result save(@RequestBody CategoryTrademarkVo categoryTrademarkVo) {

        this.baseCategoryTrademarkService.save(categoryTrademarkVo);

        return Result.ok();
    }


    @DeleteMapping("remove/{category3Id}/{tmId}")
    public Result remove(@PathVariable Long category3Id,
                         @PathVariable Long tmId) {

        this.baseCategoryTrademarkService.removeByCategory3IdAndTmId(category3Id, tmId);

        return Result.ok();
    }

}