package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

/**
 * author:atGuiGu-mqx
 * date:2022/9/13 11:35
 * 描述：
 **/
public interface OrderService extends IService<OrderInfo> {

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

    /**
     * 查看我的订单
     * @param orderInfoPage
     * @param userId
     * @return
     */
    IPage<OrderInfo> getMyOrderList(Page<OrderInfo> orderInfoPage, String userId);

    /**
     * 取消订单.
     * @param orderId
     */
    void execExpiredOrder(Long orderId);
}
