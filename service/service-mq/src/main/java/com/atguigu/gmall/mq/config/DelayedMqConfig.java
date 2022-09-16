package com.atguigu.gmall.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * author:atGuiGu-mqx
 * date:2022/9/16 14:16
 * 描述：
 **/
@Configuration
public class DelayedMqConfig {

    //  定义变量：
    public static final String exchange_delay = "exchange.delay";
    public static final String routing_delay = "routing.delay";
    public static final String queue_delay_1 = "queue.delay.1";

    //  队列
    @Bean
    public Queue delayQueue(){
        //  不需要在队列中设置延迟时间.
        return new Queue(queue_delay_1,true,false,false);
    }
    //  交换机
    @Bean
    public CustomExchange delayExchange(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("x-delayed-type","direct");
        return new CustomExchange(exchange_delay,"x-delayed-message",true,false,map);
    }
    //  绑定关系
    @Bean
    public Binding delayBinding(){
        return BindingBuilder.bind(delayQueue()).to(delayExchange()).with(routing_delay).noargs();
    }

}