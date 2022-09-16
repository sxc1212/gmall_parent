package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserInfo;

/**
 * author:atGuiGu-mqx
 * date:2022/9/8 10:14
 * 描述：
 **/
public interface UserService {

    //  登录：
    UserInfo login(UserInfo userInfo);
}
