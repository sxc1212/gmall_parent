package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class ItemServiceImpl implements ItemService {


    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public Map<String, Object> getItem(Long skuId) {

        Map<String, Object> map = new HashMap<>();


        SkuInfo skuInfo = this.productFeignClient.getSkuInfo(skuId);

        BaseCategoryView categoryView = this.productFeignClient.getCategoryView(skuInfo.getCategory3Id());

        BigDecimal skuPrice = this.productFeignClient.getSkuPrice(skuId);

        List<SpuSaleAttr> spuSaleAttrList = this.productFeignClient.getSpuSaleAttrListCheckBySku(skuId, skuInfo.getSpuId());

        List<SpuPoster> spuPosterList = this.productFeignClient.getSpuPosterBySpuId(skuInfo.getSpuId());

        Map skuValueIdsMap = this.productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());

        String strJson = JSON.toJSONString(skuValueIdsMap);

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

        map.put("skuInfo", skuInfo);
        map.put("categoryView", categoryView);
        map.put("price", skuPrice);
        map.put("spuSaleAttrList", spuSaleAttrList);
        map.put("spuPosterList", spuPosterList);
        map.put("valuesSkuJson", strJson);


        return map;
    }
}