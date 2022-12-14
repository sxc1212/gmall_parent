package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.client.ItemFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * author:atGuiGu-mqx
 * date:2022/8/31 15:35
 * 描述：
 **/
@Controller
public class ItemController {

    @Autowired
    private ItemFeignClient itemFeignClient;

    //  http://item.gmall.com/28.html
    @GetMapping("{skuId}.html")
    public String item(@PathVariable Long skuId, Model model){
        //  调用方法
        Result<Map> result = this.itemFeignClient.getItem(skuId);

        //  获取 map = result.getData();
        //  后台存储数据：
        model.addAllAttributes(result.getData());
        //  返回页面
        return "item/item";
    }

}