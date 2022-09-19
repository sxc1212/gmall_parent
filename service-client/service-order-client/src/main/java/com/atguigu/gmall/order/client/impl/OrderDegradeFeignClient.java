package com.atguigu.gmall.order.client.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.client.OrderFeignClient;
import org.springframework.stereotype.Component;


/**
 * author:atGuiGu-mqx
 * date:2022/9/13 10:49
 * 描述：
 **/
@Component
public class OrderDegradeFeignClient implements OrderFeignClient {
    @Override
    public Result authTrade() {
        return null;
    }

    @Override
    public OrderInfo getOrderInfo(Long orderId) {
        return null;
    }
}