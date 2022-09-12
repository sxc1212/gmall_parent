package com.atguigu.gmall.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.IpUtil;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/api/user/passport")
public class PassportApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/login")
    public Result login(@RequestBody UserInfo userInfo, HttpServletRequest request) {

        UserInfo info = userService.login(userInfo);
        if (info != null) {

            String token = UUID.randomUUID().toString();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("token", token);

            hashMap.put("nickName", info.getNickName());


            String userLoginKey = RedisConst.USER_LOGIN_KEY_PREFIX + token;

            String ip = IpUtil.getIpAddress(request);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", info.getId().toString());
            jsonObject.put("ip", ip);
            this.redisTemplate.opsForValue().set(userLoginKey, jsonObject.toJSONString(), RedisConst.USERKEY_TIMEOUT, TimeUnit.SECONDS);

            return Result.ok(hashMap);
        } else {
            return Result.fail().message("登录失败,请联系管理员.");
        }

    }


    @GetMapping("logout")
    public Result logout(HttpServletRequest request, @RequestHeader String token) {


        String userLoginKey = RedisConst.USER_LOGIN_KEY_PREFIX + token;

        this.redisTemplate.delete(userLoginKey);

        return Result.ok();
    }
}