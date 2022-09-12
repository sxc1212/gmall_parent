package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import com.atguigu.gmall.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public UserInfo login(UserInfo userInfo) {


        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("login_name", userInfo.getLoginName());
        String passwd = userInfo.getPasswd();
        String newPwd = MD5.encrypt(passwd);
        userInfoQueryWrapper.eq("passwd", newPwd);
        UserInfo info = userInfoMapper.selectOne(userInfoQueryWrapper);

        if (info != null) {
            return info;
        }
        return null;
    }
}