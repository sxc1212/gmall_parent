package com.atguigu.gmall.list.repository;

import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * author:atGuiGu-mqx
 * date:2022/9/6 9:06
 * 描述：
 **/
public interface GoodsRepository extends ElasticsearchRepository<Goods, Long> {
}
