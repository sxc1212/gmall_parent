package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * author:atGuiGu-mqx
 * date:2022/8/27 15:51
 * 描述： 操作的表对应的实体类。 IService
 **/
public interface BaseTrademarkService extends IService<BaseTrademark> {
    /**
     * 品牌分页列表。
     * @param baseTrademarkPage
     * @return
     */
    IPage getTradeMarkList(Page<BaseTrademark> baseTrademarkPage);
}
