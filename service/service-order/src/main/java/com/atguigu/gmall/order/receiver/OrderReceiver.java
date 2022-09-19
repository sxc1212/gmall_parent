package com.atguigu.gmall.order.receiver;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.constant.MqConst;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * author:atGuiGu-mqx
 * date:2022/9/16 15:39
 * 描述：
 **/
@Component
public class OrderReceiver {

    @Autowired
    private OrderService orderService;

    //  监听消息：
    @SneakyThrows
    @RabbitListener(queues = MqConst.QUEUE_ORDER_CANCEL)
    public void orderCancel(Long orderId, Message message, Channel channel){
        try {
            //  判断  orderInfo ;  paymentInfo;  Alipay
            if (orderId!=null){
                //  根据订单Id 获取订单对象
                OrderInfo orderInfo = orderService.getById(orderId);
                if (orderInfo!=null && "UNPAID".equals(orderInfo.getOrderStatus()) && "UNPAID".equals(orderInfo.getProcessStatus())){
                    //  更新订单的状态。 CLOSED;
                    orderService.execExpiredOrder(orderId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //  手动确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

    //  监听消息更新订单状态
    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_PAYMENT_PAY,durable = "true",autoDelete = "false"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_PAYMENT_PAY),
            key = {MqConst.ROUTING_PAYMENT_PAY}
    ))
    public void updateOrderStatus(Long orderId,Message message, Channel channel){
        try {
            if (orderId!=null){
                //  根据订单Id 获取订单对象
                OrderInfo orderInfo = orderService.getById(orderId);
                //  相当于根据业务字段状态保证不重复消费消息！
                //                if ("PAID".equals(orderInfo.getOrderStatus())){
                //                    return;
                //                }
                if ("UNPAID".equals(orderInfo.getOrderStatus())){
                    //  更新订单状态.
                    this.orderService.updateOrderStatus(orderId, ProcessStatus.PAID);
                    //  发送消息给订单：
                    this.orderService.sendOrderMsg(orderId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //  手动确认：
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

    //  监听减库存结果：
    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_WARE_ORDER,durable = "true",autoDelete = "false"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_WARE_ORDER),
            key = {MqConst.ROUTING_WARE_ORDER}
    ))
    public void wareOrder(String strJson,Message message, Channel channel){
        try {
            if (!StringUtils.isEmpty(strJson)){
                //  strJson --> map
                Map map = JSON.parseObject(strJson, Map.class);
                String orderId = (String) map.get("orderId");
                String status = (String) map.get("status");
                //  判断减库存结果：
                if ("DEDUCTED".equals(status)){
                    //  减库存成功 更新状态
                    this.orderService.updateOrderStatus(Long.parseLong(orderId),ProcessStatus.WAITING_DELEVER);
                }else {
                    //  减库存失败！ 并没有调用退款方法，也没有关闭订单！ 而是使用 mq 做数据的最终一致性！
                    //  减库存失败！ 并没有调用退款方法，也没有关闭订单！
                    //  记录日志，记录数据表，哪个订单，减库存失败。 赶紧补货！ 当补货成功之后，手动发送消息更改库存状态！
                    //  订单 -- 支付 -- 库存，我们都是走的异步消息 利用mq 保证数据的最终一致性！ 分布式事务！
                    //  如果补货失败！ 调用人工客服功能，协商！ 协商失败，退款接口!
                    this.orderService.updateOrderStatus(Long.parseLong(orderId),ProcessStatus.STOCK_EXCEPTION);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        //  手动确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

}