package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;


public interface ManageService {
    
    List<BaseCategory1> getCategory1();

    
    List<BaseCategory2> getCategory2(Long category1Id);

    
    List<BaseCategory3> getCategory3(Long category2Id);

    
    List<BaseAttrInfo> getAttrInfoList(Long category1Id, Long category2Id, Long category3Id);

    
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    
    List<BaseAttrValue> getAttrValueList(Long attrId);

    
    BaseAttrInfo getAttrInfo(Long attrId);

    
    IPage<SpuInfo> getSpuList(Page<SpuInfo> spuInfoPage, SpuInfo spuInfo);

    
    List<BaseSaleAttr> getBaseSaleAttrList();

    
    void saveSpuInfo(SpuInfo spuInfo);

    
    List<SpuImage> getSpuImageList(Long spuId);

    
    List<SpuSaleAttr> getSpuSaleAttrList(Long spuId);

    
    void saveSkuInfo(SkuInfo skuInfo);

    
    IPage getSkuInfoList(Page<SkuInfo> skuInfoPage, SkuInfo skuInfo);

    
    void onSale(Long skuId);

    
    void cancelSale(Long skuId);
}
