package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jodd.util.Consumers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ManageServiceImpl implements ManageService {


    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;

    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;

    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    private SpuImageMapper spuImageMapper;

    @Autowired
    private SpuPosterMapper spuPosterMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;


    @Override
    public List<BaseCategory1> getCategory1() {


        return baseCategory1Mapper.selectList(null);
    }

    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {


        return baseCategory2Mapper.selectList(new QueryWrapper<BaseCategory2>().eq("category1_id", category1Id));
    }


    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {


        return baseCategory3Mapper.selectList(new QueryWrapper<BaseCategory3>().eq("category2_id", category2Id));
    }

    @Override
    public List<BaseAttrInfo> getAttrInfoList(Long category1Id, Long category2Id, Long category3Id) {

        return baseAttrInfoMapper.selectAttrInfoList(category1Id, category2Id, category3Id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {

        if (baseAttrInfo.getId() != null) {

            baseAttrInfoMapper.updateById(baseAttrInfo);

            QueryWrapper<BaseAttrValue> baseAttrValueQueryWrapper = new QueryWrapper<>();
            baseAttrValueQueryWrapper.eq("attr_id", baseAttrInfo.getId());
            baseAttrValueMapper.delete(baseAttrValueQueryWrapper);
        } else {


            baseAttrInfoMapper.insert(baseAttrInfo);
        }


        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();

        if (!CollectionUtils.isEmpty(attrValueList)) {

            attrValueList.forEach(baseAttrValue -> {


                baseAttrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insert(baseAttrValue);
            });
        }
    }

    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {


        QueryWrapper<BaseAttrValue> baseAttrValueQueryWrapper = new QueryWrapper<>();
        baseAttrValueQueryWrapper.eq("attr_id", attrId);
        return baseAttrValueMapper.selectList(baseAttrValueQueryWrapper);
    }

    @Override
    public BaseAttrInfo getAttrInfo(Long attrId) {

        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectById(attrId);
        if (baseAttrInfo != null) {

            baseAttrInfo.setAttrValueList(this.getAttrValueList(attrId));
        }

        return baseAttrInfo;
    }

    @Override
    public IPage<SpuInfo> getSpuList(Page<SpuInfo> spuInfoPage, SpuInfo spuInfo) {

        QueryWrapper<SpuInfo> spuInfoQueryWrapper = new QueryWrapper<>();
        spuInfoQueryWrapper.eq("category3_id", spuInfo.getCategory3Id());
        spuInfoQueryWrapper.orderByDesc("id");
        return spuInfoMapper.selectPage(spuInfoPage, spuInfoQueryWrapper);
    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {

        return baseSaleAttrMapper.selectList(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSpuInfo(SpuInfo spuInfo) {


        spuInfoMapper.insert(spuInfo);

        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if (!CollectionUtils.isEmpty(spuImageList)) {
            spuImageList.forEach(spuImage -> {

                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insert(spuImage);
            });
        }

        List<SpuPoster> spuPosterList = spuInfo.getSpuPosterList();
        if (!CollectionUtils.isEmpty(spuPosterList)) {
            spuPosterList.forEach(spuPoster -> {
                spuPoster.setSpuId(spuInfo.getId());
                spuPosterMapper.insert(spuPoster);
            });
        }

        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (!CollectionUtils.isEmpty(spuSaleAttrList)) {
            spuSaleAttrList.forEach(spuSaleAttr -> {
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insert(spuSaleAttr);

                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if (!CollectionUtils.isEmpty(spuSaleAttrValueList)) {
                    spuSaleAttrValueList.forEach(spuSaleAttrValue -> {
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                        spuSaleAttrValueMapper.insert(spuSaleAttrValue);
                    });
                }
            });
        }
    }

    @Override
    public List<SpuImage> getSpuImageList(Long spuId) {

        return spuImageMapper.selectList(new QueryWrapper<SpuImage>().eq("spu_id", spuId));
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(Long spuId) {

        return spuSaleAttrMapper.selectSpuSaleAttrList(spuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSkuInfo(SkuInfo skuInfo) {

        try {

            this.skuInfoMapper.insert(skuInfo);

            List<SkuImage> skuImageList = skuInfo.getSkuImageList();
            if (!CollectionUtils.isEmpty(skuImageList)) {
                skuImageList.forEach(skuImage -> {
                    skuImage.setSkuId(skuInfo.getId());
                    skuImageMapper.insert(skuImage);
                });
            }


            List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
            if (!CollectionUtils.isEmpty(skuAttrValueList)) {
                skuAttrValueList.forEach(skuAttrValue -> {

                    skuAttrValue.setSkuId(skuInfo.getId());
                    skuAttrValueMapper.insert(skuAttrValue);
                });
            }


            List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
            if (!CollectionUtils.isEmpty(skuSaleAttrValueList)) {
                skuSaleAttrValueList.forEach(skuSaleAttrValue -> {
                    skuSaleAttrValue.setSkuId(skuInfo.getId());
                    skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
                    skuSaleAttrValueMapper.insert(skuSaleAttrValue);
                });
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Override
    public IPage getSkuInfoList(Page<SkuInfo> skuInfoPage, SkuInfo skuInfo) {


        QueryWrapper<SkuInfo> skuInfoQueryWrapper = new QueryWrapper<>();
        skuInfoQueryWrapper.eq("category3_id", skuInfo.getCategory3Id());
        skuInfoQueryWrapper.orderByDesc("id");
        return skuInfoMapper.selectPage(skuInfoPage, skuInfoQueryWrapper);
    }

    @Override
    public void onSale(Long skuId) {


        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(1);
        this.skuInfoMapper.updateById(skuInfo);
    }

    @Override
    public void cancelSale(Long skuId) {

        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(0);
        this.skuInfoMapper.updateById(skuInfo);
    }

    @Override
    public SkuInfo getSkuInfo(Long skuId) {


        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);


        QueryWrapper<SkuImage> skuImageQueryWrapper = new QueryWrapper<>();
        skuImageQueryWrapper.eq("sku_id", skuId);
        List<SkuImage> skuImageList = skuImageMapper.selectList(skuImageQueryWrapper);
        skuInfo.setSkuImageList(skuImageList);

        return skuInfo;
    }

    @Override
    public BaseCategoryView getBaseCategoryView(Long category3Id) {

        return baseCategoryViewMapper.selectById(category3Id);
    }

    @Override
    public BigDecimal getSkuPrice(Long skuId) {


        QueryWrapper<SkuInfo> skuInfoQueryWrapper = new QueryWrapper<>();
        skuInfoQueryWrapper.eq("id", skuId);

        skuInfoQueryWrapper.select("price");
        SkuInfo skuInfo = skuInfoMapper.selectOne(skuInfoQueryWrapper);
        if (skuInfo != null) {
            return skuInfo.getPrice();
        }


        return new BigDecimal("0");
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {

        return spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(skuId, spuId);
    }

    @Override
    public Map getSkuValueIdsMap(Long spuId) {

        HashMap<Object, Object> hashMap = new HashMap<>();

        List<Map> mapList = skuSaleAttrValueMapper.selectSkuValueIdsMap(spuId);
        if (!CollectionUtils.isEmpty(mapList)) {
            mapList.forEach(map -> {

                hashMap.put(map.get("value_ids"), map.get("sku_id"));
            });
        }

        return hashMap;
    }

    @Override
    public List<SpuPoster> findSpuPosterBySpuId(Long spuId) {
        return spuPosterMapper.selectList(new QueryWrapper<SpuPoster>().eq("spu_id", spuId));
    }

    @Override
    public List<BaseAttrInfo> getAttrList(Long skuId) {

        return baseAttrInfoMapper.selectAttrList(skuId);
    }


}