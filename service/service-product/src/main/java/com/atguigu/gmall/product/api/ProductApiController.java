package com.atguigu.gmall.product.api;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.atguigu.gmall.product.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("api/product")
public class ProductApiController {

    @Autowired
    private ManageService manageService;


    @Autowired
    private BaseTrademarkService baseTrademarkService;


    @GetMapping("inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable Long skuId) {

        return this.manageService.getSkuInfo(skuId);
    }


    @GetMapping("inner/getCategoryView/{category3Id}")
    public BaseCategoryView getBaseCategoryView(@PathVariable Long category3Id) {

        return this.manageService.getBaseCategoryView(category3Id);
    }


    @GetMapping("inner/getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(@PathVariable Long skuId) {
        return this.manageService.getSkuPrice(skuId);
    }


    @GetMapping("inner/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable Long skuId,
                                                          @PathVariable Long spuId) {

        return this.manageService.getSpuSaleAttrListCheckBySku(skuId, spuId);
    }


    @GetMapping("inner/getSkuValueIdsMap/{spuId}")
    public Map getSkuValueIdsMap(@PathVariable Long spuId) {
        return this.manageService.getSkuValueIdsMap(spuId);
    }


    @GetMapping("inner/findSpuPosterBySpuId/{spuId}")
    public List<SpuPoster> findSpuPosterBySpuId(@PathVariable Long spuId) {

        return this.manageService.findSpuPosterBySpuId(spuId);
    }


    @GetMapping("inner/getAttrList/{skuId}")
    public List<BaseAttrInfo> getAttrList(@PathVariable Long skuId) {
        return this.manageService.getAttrList(skuId);
    }


    @GetMapping("getBaseCategoryList")
    public Result getBaseCategoryList() {

        List<JSONObject> list = this.manageService.getBaseCategoryList();
        return Result.ok(list);
    }


    @GetMapping("inner/getTrademark/{tmId}")
    public BaseTrademark getTradeMark(@PathVariable Long tmId) {

        return baseTrademarkService.getById(tmId);
    }


}