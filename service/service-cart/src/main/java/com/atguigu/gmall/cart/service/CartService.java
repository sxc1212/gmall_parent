package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;

import java.util.List;


public interface CartService {

    void addToCart(Long skuId, String userId, Integer skuNum);


    List<CartInfo> getCartList(String userId, String userTempId);


    void checkCart(Long skuId, String userId, Integer isChecked);


    void deleteCart(Long skuId, String userId);
}
