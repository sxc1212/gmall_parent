package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;

/**
 * author:atGuiGu-mqx
 * date:2022/9/13 11:35
 * 描述：
 **/
public interface OrderService {

    /**
     * 保存订单
     * @param orderInfo
     * @return
     */
    Long saveOrderInfo(OrderInfo orderInfo);

    //  返回流水号！
    String getTradeNo(String userId);

    /**
     * 比较流水号
     * @param userId 获取缓存的流水号
     * @param tradeNo 页面流水号
     * @return
     */
    Boolean checkTradeNo(String tradeNo, String userId);

    /**
     * 删除流水号
     * @param userId
     */
    void delTradeNo(String userId);

    /**
     * 校验库存系统
     * @param skuId
     * @param skuNum
     * @return
     */
    Boolean checkStock(Long skuId, Integer skuNum);
}
