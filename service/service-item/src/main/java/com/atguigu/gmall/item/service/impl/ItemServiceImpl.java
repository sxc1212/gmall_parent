package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;


@Service
public class ItemServiceImpl implements ItemService {


    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public Map<String, Object> getItem(Long skuId) {

        Map<String, Object> map = new HashMap<>();






        CompletableFuture<SkuInfo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfo skuInfo = this.productFeignClient.getSkuInfo(skuId);
            map.put("skuInfo",skuInfo);

            return skuInfo;
        },threadPoolExecutor);


        CompletableFuture<Void> categoryViewCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            BaseCategoryView categoryView = this.productFeignClient.getCategoryView(skuInfo.getCategory3Id());
            map.put("categoryView",categoryView);
        },threadPoolExecutor);

        CompletableFuture<Void> priceCompletableFuture = CompletableFuture.runAsync(() -> {
            BigDecimal skuPrice = this.productFeignClient.getSkuPrice(skuId);
            map.put("price", skuPrice);
        },threadPoolExecutor);


        CompletableFuture<Void> spuSaleAttrListCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            List<SpuSaleAttr> spuSaleAttrList = this.productFeignClient.getSpuSaleAttrListCheckBySku(skuId, skuInfo.getSpuId());
            map.put("spuSaleAttrList", spuSaleAttrList);

        },threadPoolExecutor);

        CompletableFuture<Void> spuPosterListCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            List<SpuPoster> spuPosterList = this.productFeignClient.getSpuPosterBySpuId(skuInfo.getSpuId());
            map.put("spuPosterList", spuPosterList);
        },threadPoolExecutor);

        CompletableFuture<Void> skuJsonCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            Map skuValueIdsMap = this.productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());

            String strJson = JSON.toJSONString(skuValueIdsMap);
            map.put("valuesSkuJson", strJson);
        },threadPoolExecutor);


        CompletableFuture<Void> attrListCompletableFuture = CompletableFuture.runAsync(() -> {
            List<BaseAttrInfo> attrList = this.productFeignClient.getAttrList(skuId);

            if (!CollectionUtils.isEmpty(attrList)) {
                List<HashMap<String, Object>> attrMapList = attrList.stream().map(baseAttrInfo -> {

                    HashMap<String, Object> hashMap = new HashMap<>();

                    hashMap.put("attrName", baseAttrInfo.getAttrName());
                    hashMap.put("attrValue", baseAttrInfo.getAttrValueList().get(0).getValueName());
                    return hashMap;
                }).collect(Collectors.toList());

                map.put("skuAttrList", attrMapList);
            }
        },threadPoolExecutor);

        CompletableFuture.allOf(
                skuInfoCompletableFuture,
                categoryViewCompletableFuture,
                spuSaleAttrListCompletableFuture,
                priceCompletableFuture,
                spuPosterListCompletableFuture,
                skuJsonCompletableFuture,
                attrListCompletableFuture
                ).join();


        return map;
    }
}