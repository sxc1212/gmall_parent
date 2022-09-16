package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;

import java.util.List;

/**
 * author:atGuiGu-mqx
 * date:2022/9/9 10:10
 * 描述：
 **/
public interface CartService {
    /**
     *
     * @param skuId
     * @param userId
     * @param skuNum
     */
    void addToCart(Long skuId, String userId, Integer skuNum);

    /**
     * 查看购物车列表
     * @param userId
     * @param userTempId
     * @return
     */
    List<CartInfo> getCartList(String userId, String userTempId);

    /**
     * 修改选中状态.
     * @param skuId
     * @param userId
     * @param isChecked
     */
    void checkCart(Long skuId, String userId, Integer isChecked);

    /**
     * 删除购物车
     * @param skuId
     */
    void deleteCart(Long skuId,String userId);

    /**
     * 根据用户Id 来获取选中状态的购物项
     * @param userId
     * @return
     */
    List<CartInfo> getCartCheckedList(String userId);
}
