package com.atguigu.gmall.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.constant.MqConst;
import com.atguigu.gmall.common.service.RabbitService;
import com.atguigu.gmall.common.util.HttpClientUtil;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * author:atGuiGu-mqx
 * date:2022/9/13 11:35
 * 描述：
 **/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderInfoMapper,OrderInfo> implements OrderService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitService rabbitService;

    /*
    ware:
  url: http://localhost:9001
     */
    @Value("${ware.url}")
    private String wareUrl;  // http://localhost:9001

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrderInfo(OrderInfo orderInfo) {
        //  order_info total_amount order_status user_id out_trade_no trade_body operate_time expire_time process_status
        orderInfo.sumTotalAmount(); // total_amount
        //  order_status
        orderInfo.setOrderStatus(OrderStatus.UNPAID.name());
        //  第三方交易编号 ,必须保证不能重复.
        String outTradeNo = "ATGUIGU"+System.currentTimeMillis()+ new Random().nextInt(10000);
        orderInfo.setOutTradeNo(outTradeNo);
        //  订单的描述信息： 将商品的名称定义为订单的描述信息.
        orderInfo.setTradeBody("购买国产手机咔咔咔香");
        orderInfo.setOperateTime(new Date());
        //  过期时间：所有的商品默认为24小时:
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,1);
        orderInfo.setExpireTime(calendar.getTime());
        //  进度状态：
        orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());
        orderInfoMapper.insert(orderInfo);
        //  获取到订单Id
        Long orderId = orderInfo.getId();
        //  order_detail
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        if (!CollectionUtils.isEmpty(orderDetailList)){
            orderDetailList.forEach(orderDetail -> {
                //  细节：
                orderDetail.setOrderId(orderId);
                orderDetailMapper.insert(orderDetail);

            });
        }

        //  发送一个延迟消息：
        this.rabbitService.sendDelayMsg(MqConst.EXCHANGE_DIRECT_ORDER_CANCEL,MqConst.ROUTING_ORDER_CANCEL,orderId,MqConst.DELAY_TIME);
        //  返回订单Id
        return orderId;
    }

    @Override
    public String getTradeNo(String userId) {
        //  key 不能重复！
        String key = "tradeNo:"+userId;
        //  声明一个变量接收流水号！
        String tradeNo = UUID.randomUUID().toString();
        //  存储到redis 中！
        this.redisTemplate.opsForValue().set(key,tradeNo);
        //  返回流水号！
        return tradeNo;
    }

    @Override
    public Boolean checkTradeNo(String tradeNo,String userId) {
        //  key 不能重复！
        String key = "tradeNo:"+userId;
        //  获取缓存流水号判断
        String redisTradeNo = (String) this.redisTemplate.opsForValue().get(key);

        //  返回比较结果.
        return tradeNo.equals(redisTradeNo);
    }

    @Override
    public void delTradeNo(String userId) {
        //  key 不能重复！
        String key = "tradeNo:"+userId;
        this.redisTemplate.delete(key);
    }

    @Override
    public Boolean checkStock(Long skuId, Integer skuNum) {
        //  远程调用库存系统接口: http://localhost:9001/hasStock?skuId=10221&num=2
        // http://localhost:9001
        String res = HttpClientUtil.doGet(wareUrl + "/hasStock?skuId=" + skuId + "&num=" + skuNum);
        //  返回比较结果。
        return "1".equals(res);
    }

    @Override
    public IPage<OrderInfo> getMyOrderList(Page<OrderInfo> orderInfoPage, String userId) {
        //  两张表： orderInfo ,orderDetail;
        IPage<OrderInfo> infoIPage = orderInfoMapper.selectMyOrder(orderInfoPage,userId);
        infoIPage.getRecords().forEach(orderInfo -> {
            String statusName = OrderStatus.getStatusNameByStatus(orderInfo.getOrderStatus());
            orderInfo.setOrderStatusName(statusName);
        });
        return infoIPage;
    }

    @Override
    public void execExpiredOrder(Long orderId) {
        //  本质更新订单状态
        //  根据订单Id , 更新订单状态 {PAID ,SPLIT}
        this.updateOrderStatus(orderId,ProcessStatus.CLOSED);

        //  退款的时候的： 关闭paymentInfo orderInfo ; 远程调用 sendMsg
        this.rabbitService.sendMsg(MqConst.EXCHANGE_DIRECT_PAYMENT_CLOSE,MqConst.ROUTING_PAYMENT_CLOSE,orderId);
    }

    @Override
    public OrderInfo getOrderInfo(Long orderId) {
        //  获取订单对象
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        //  判断防止空指针
        if (orderInfo!=null){
            //  获取订单明细
            List<OrderDetail> orderDetailList = this.orderDetailMapper.selectList(new QueryWrapper<OrderDetail>().eq("order_id", orderId));
            orderInfo.setOrderDetailList(orderDetailList);
        }
        //  返回数据
        return orderInfo;
    }

    /**
     * 根据订单Id 修改 订单状态.
     * @param orderId
     * @param processStatus
     */
    public void updateOrderStatus(Long orderId, ProcessStatus processStatus) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(orderId);
        //  订单 状态
        orderInfo.setOrderStatus(processStatus.getOrderStatus().name());
        //  更新进度状态  ---  进度状态中能获取到订单状态.
        orderInfo.setProcessStatus(processStatus.name());
        orderInfo.setUpdateTime(new Date());
        this.orderInfoMapper.updateById(orderInfo);
    }

    @Override
    public void sendOrderMsg(Long orderId) {
        //  更改状态.
        this.updateOrderStatus(orderId,ProcessStatus.NOTIFIED_WARE);
        //  构成json 字符串  orderInfo  --->  json 但是，orderInfo 中有很多不需要的字段 ；
        //  orderInfo --->  map ---> json
        //  orderInfo 必须包含orderDetailList
        OrderInfo orderInfo = this.getOrderInfo(orderId);
        //   orderInfo --->  map
        Map map = this.wareJson(orderInfo);
        //  发送消息：
        this.rabbitService.sendMsg(MqConst.EXCHANGE_DIRECT_WARE_STOCK,MqConst.ROUTING_WARE_STOCK, JSON.toJSONString(map));
    }

    /**
     * 将orderInfo 中的部分字段改为 map
     * @param orderInfo
     * @return
     */
    public Map wareJson(OrderInfo orderInfo) {
        //  声明map 集合
        Map<String,Object> map = new HashMap<>();
        //  给map 集合赋值操作.
        map.put("orderId", orderInfo.getId());
        map.put("consignee", orderInfo.getConsignee());
        map.put("consigneeTel", orderInfo.getConsigneeTel());
        map.put("orderComment", orderInfo.getOrderComment());
        map.put("orderBody", orderInfo.getTradeBody());
        map.put("deliveryAddress", orderInfo.getDeliveryAddress());
        map.put("paymentWay", "2");

        //  wareId	 传入时的仓库编号;
        map.put("wareId",orderInfo.getWareId());
        //  赋值订单明细：
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        //  List<Map>
        List<HashMap<String, Object>> detailList = orderDetailList.stream().map(orderDetail -> {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("skuId", orderDetail.getSkuId());
            hashMap.put("skuNum", orderDetail.getSkuNum());
            hashMap.put("skuName", orderDetail.getSkuName());
            return hashMap;
        }).collect(Collectors.toList());
        map.put("details", detailList);
        //  返回map 集合
        return map;
    }

    @Override
    public List<OrderInfo> orderSplit(String orderId, String wareSkuMap) {
        /*
            1.  先获取到原始订单
            2.  [{"wareId":"1","skuIds":["2","10"]},{"wareId":"2","skuIds":["3"]}] 参数变为能操作的对象！
            3.  获取子订单 并且给子订单赋值
            4.  重新保存子订单
            5.  将子订单添加到集合中
            6.  更改原始订单状态

         */
        List<OrderInfo> orderInfoList = new ArrayList<>();

        OrderInfo orderInfoOrigin = this.getOrderInfo(Long.parseLong(orderId));
        List<Map> mapList = JSON.parseArray(wareSkuMap, Map.class);
        //  判断
        if (!CollectionUtils.isEmpty(mapList)){
            //  循环遍历
            for (Map map : mapList) {
                //  获取仓库Id
                String wareId = (String) map.get("wareId");
                //  获取仓库Id 中对应的skuId
                List<String> skuIdsList = (List<String>) map.get("skuIds");
                //  声明一个子订单对象
                OrderInfo subOrderInfo = new OrderInfo();
                //  属于同一个实体类
                BeanUtils.copyProperties(orderInfoOrigin,subOrderInfo);
                //  防止主键冲突
                subOrderInfo.setId(null);
                //  父id
                subOrderInfo.setParentOrderId(Long.parseLong(orderId));
                //  赋值仓库Id
                subOrderInfo.setWareId(wareId);
                //  赋值子订单订单明细  子订单明细是从原始订单明细中获取的！
                //  分析原始订单明细：2 3 10
                //  [{"wareId":"1","skuIds":["2","10"]},{"wareId":"2","skuIds":["3"]}]
                List<OrderDetail> orderDetailList = orderInfoOrigin.getOrderDetailList().stream().filter((orderDetail -> {
                    return skuIdsList.contains(orderDetail.getSkuId().toString());
                })).collect(Collectors.toList());
                //  赋值子订单明细
                subOrderInfo.setOrderDetailList(orderDetailList);
                //  计算总金额
                subOrderInfo.sumTotalAmount();
                //  保存子订单
                this.saveOrderInfo(subOrderInfo);
                //  添加子订单到集合中
                orderInfoList.add(subOrderInfo);
            }
        }
        //  更新原始订单状态.
        this.updateOrderStatus(Long.parseLong(orderId),ProcessStatus.SPLIT);
        //  返回子订单集合
        return orderInfoList;
    }
}