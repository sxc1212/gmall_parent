package com.atguigu.gmall.client.impl;

import com.atguigu.gmall.client.ListFeignClient;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.SearchParam;
import org.springframework.stereotype.Component;

/**
 * author:atGuiGu-mqx
 * date:2022/9/6 14:11
 * 描述：
 **/
@Component
public class ListDegradeFeignClient implements ListFeignClient {
    @Override
    public Result incrHotScore(Long skuId) {
        return null;
    }

    @Override
    public Result list(SearchParam listParam) {
        return null;
    }

    @Override
    public Result upperGoods(Long skuId) {
        return null;
    }

    @Override
    public Result lowerGoods(Long skuId) {
        return null;
    }
}