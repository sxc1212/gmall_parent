package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategoryTrademark;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.CategoryTrademarkVo;
import com.atguigu.gmall.product.mapper.BaseCategoryTrademarkMapper;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.product.service.BaseCategoryTrademarkService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class BaseCategoryTrademarkServiceImpl extends ServiceImpl<BaseCategoryTrademarkMapper, BaseCategoryTrademark> implements BaseCategoryTrademarkService {

    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;

    @Autowired
    private BaseCategoryTrademarkMapper baseCategoryTrademarkMapper;

    @Override
    public List<BaseTrademark> findTrademarkList(Long category3Id) {


        QueryWrapper<BaseCategoryTrademark> baseCategoryTrademarkQueryWrapper = new QueryWrapper<>();
        baseCategoryTrademarkQueryWrapper.eq("category3_id", category3Id);
        List<BaseCategoryTrademark> baseCategoryTrademarkList = baseCategoryTrademarkMapper.selectList(baseCategoryTrademarkQueryWrapper);


        if (!CollectionUtils.isEmpty(baseCategoryTrademarkList)) {


            List<Long> tmIdsList = baseCategoryTrademarkList.stream().map(BaseCategoryTrademark::getTrademarkId).collect(Collectors.toList());


            List<BaseTrademark> baseTrademarkList = baseTrademarkMapper.selectBatchIds(tmIdsList);
            return baseTrademarkList;
        }
        return null;
    }

    @Override
    public List<BaseTrademark> findCurrentTrademarkList(Long category3Id) {


        QueryWrapper<BaseCategoryTrademark> baseCategoryTrademarkQueryWrapper = new QueryWrapper<>();
        baseCategoryTrademarkQueryWrapper.eq("category3_id", category3Id);
        List<BaseCategoryTrademark> baseCategoryTrademarkList = baseCategoryTrademarkMapper.selectList(baseCategoryTrademarkQueryWrapper);

        if (!CollectionUtils.isEmpty(baseCategoryTrademarkList)) {

            List<Long> tmIdsList = baseCategoryTrademarkList.stream().map(BaseCategoryTrademark::getTrademarkId).collect(Collectors.toList());


            List<BaseTrademark> baseTrademarkList = baseTrademarkMapper.selectList(null).stream().filter(baseTrademark -> {
                return !tmIdsList.contains(baseTrademark.getId());
            }).collect(Collectors.toList());

            return baseTrademarkList;
        }

        return baseTrademarkMapper.selectList(null);
    }

    @Override
    public void save(CategoryTrademarkVo categoryTrademarkVo) {


        List<Long> trademarkIdList = categoryTrademarkVo.getTrademarkIdList();


        if (!CollectionUtils.isEmpty(trademarkIdList)) {


            List<BaseCategoryTrademark> baseCategoryTrademarkArrayList = trademarkIdList.stream().map(tmId -> {
                BaseCategoryTrademark baseCategoryTrademark = new BaseCategoryTrademark();
                baseCategoryTrademark.setCategory3Id(categoryTrademarkVo.getCategory3Id());
                baseCategoryTrademark.setTrademarkId(tmId);
                return baseCategoryTrademark;
            }).collect(Collectors.toList());

            this.saveBatch(baseCategoryTrademarkArrayList);
        }
    }

    @Override
    public void removeByCategory3IdAndTmId(Long category3Id, Long tmId) {

        QueryWrapper<BaseCategoryTrademark> baseCategoryTrademarkQueryWrapper = new QueryWrapper<>();
        baseCategoryTrademarkQueryWrapper.eq("category3_id", category3Id);
        baseCategoryTrademarkQueryWrapper.eq("trademark_id", tmId);
        baseCategoryTrademarkMapper.delete(baseCategoryTrademarkQueryWrapper);
    }
}