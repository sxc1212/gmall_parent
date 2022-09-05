package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * author:atGuiGu-mqx
 * date:2022/8/30 10:24
 * 描述：
 **/
@Mapper
public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {
    /**
     * 根据spuId 获取数据
     * @param spuId
     * @return
     */
    List<Map> selectSkuValueIdsMap(Long spuId);
}
