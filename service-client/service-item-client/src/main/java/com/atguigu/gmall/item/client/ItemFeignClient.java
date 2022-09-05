package com.atguigu.gmall.item.client;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.client.impl.ItemDegradeFeignClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * author:atGuiGu-mqx
 * date:2022/8/31 15:23
 * 描述：
 **/
@FeignClient(value = "service-item",fallback = ItemDegradeFeignClient.class)
public interface ItemFeignClient {

    //  获取service-item 微服务数据接口：
    @GetMapping("/api/item/{skuId}")
    Result getItem(@PathVariable Long skuId);
}
