package com.atguigu.gmall.list.service.impl;

import com.atguigu.gmall.list.service.SearchService;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;


@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private ProductFeignClient productFeignClient;




    @Override
    public void upperGoods(Long skuId) {

    }

    @Override
    public void lowerGoods(Long skuId) {

    }
}