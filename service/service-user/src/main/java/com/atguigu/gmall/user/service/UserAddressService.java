package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserAddress;

import java.util.List;

/**
 * author:atGuiGu-mqx
 * date:2022/9/13 9:28
 * 描述：
 **/
public interface UserAddressService {
    /**
     * 获取用户的收货地址列表.
     * @param userId
     * @return
     */
    List<UserAddress> getUserAddressListByUserId(String userId);

}
