package com.atguigu.gmall.list.service;

import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;

import java.io.IOException;

/**
 * author:atGuiGu-mqx
 * date:2022/9/4 15:52
 * 描述：
 **/
public interface SearchService {

    //  上架：参数 返回值！
    void upperGoods(Long skuId);
    //  下架
    void lowerGoods(Long skuId);
    //  更新es热度
    void incrHotScore(Long skuId);
    //  查询
    SearchResponseVo search(SearchParam searchParam) throws IOException;
}
