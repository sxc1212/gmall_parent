package com.atguigu.gmall.item.service;

import java.util.Map;

/**
 * author:atGuiGu-mqx
 * date:2022/8/30 15:53
 * 描述：
 **/
public interface ItemService {
    /**
     * 根据skuId 获取渲染数据
     * @param skuId
     * @return
     */
    Map<String, Object> getItem(Long skuId);
}
