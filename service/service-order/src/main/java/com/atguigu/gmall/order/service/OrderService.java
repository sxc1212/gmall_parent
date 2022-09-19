package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

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

    /**
     * 根据订单Id 获取订单信息
     * @param orderId
     * @return
     */
    OrderInfo getOrderInfo(Long orderId);

    /**
     * 根据订单Id 更新状态.
     * @param orderId
     * @param processStatus
     */
    void updateOrderStatus(Long orderId, ProcessStatus processStatus);

    /**
     * 发送消息给库存系统
     * @param orderId
     */
    void sendOrderMsg(Long orderId);

    /**
     * 将orderInfo 转换为Map
     * @param orderInfo
     * @return
     */
    Map wareJson(OrderInfo orderInfo);

    /**
     * 拆单
     * @param orderId
     * @param wareSkuMap
     * @return
     */
    List<OrderInfo> orderSplit(String orderId, String wareSkuMap);
}
