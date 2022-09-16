package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * author:atGuiGu-mqx
 * date:2022/8/26 11:38
 * 描述：
 **/
@Mapper
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {
    /**
     * 根据分类Id 查询平台属性
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    List<BaseAttrInfo> selectAttrInfoList(@Param("category1Id") Long category1Id,
                                          @Param("category2Id") Long category2Id,
                                          @Param("category3Id") Long category3Id);

    /**
     *
     * @param skuId
     * @return
     */
    List<BaseAttrInfo> selectAttrList(Long skuId);
}
