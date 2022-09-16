package com.atguigu.gmall.all.controller;

import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;

/**
 * author:atGuiGu-mqx
 * date:2022/9/8 11:19
 * 描述：
 **/
@Controller
public class PassportController {

    //  http://passport.gmall.com/login.html?originUrl=http://item.gmall.com/26.html

    @SneakyThrows
    @GetMapping("login.html")
    public String login(HttpServletRequest request){
        String originUrl = request.getParameter("originUrl");
        //  System.out.println(request.getRequestURI());
        //  String encode = URLEncoder.encode(originUrl, "UTF-8");
        //  System.out.println(encode);
        //  System.out.println("originUrl:\t"+originUrl);
        //  ${originUrl}
        String queryString = request.getQueryString();
        request.setAttribute("originUrl",queryString.substring(queryString.indexOf("=")+1));
        //  返回登录页面.
        return "login";
    }
}