package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * author:atGuiGu-mqx
 * date:2022/8/27 15:25
 * 描述：
 **/
@RestController // 组合注解 @ResponseBody @Controller    @ResponseBody：a.返回json 数据   b. 能将数据直接显示到页面！
@RequestMapping("admin/product/") // 表示在当前这个类中 ，有很多映射路径。所有的映射路径都是以 admin/product/
public class SpuManageController {

    @Autowired
    private ManageService manageService;

    //  http://localhost/admin/product/1/5?category3Id=61
    //  http://localhost/admin/product/1/5?category3Id=61
    //  springmvc 获取数据：
    //      1.  @RequestParam Long category3Id,
    //      2.  对象传值： 参数名称与实体类的属性名一致，则会自动映射！
    //                  /admin/product/{page}/{limit}
    /**
     * 根据三级分类Id 查询spu 列表！
     * @param page  第几页 1
     * @param limit 每页显示的条数 5
     * @return
     */
    @GetMapping("{page}/{limit}")
    public Result getSpuList(@PathVariable Long page,
                             @PathVariable Long limit,
                             SpuInfo spuInfo
                             ){

        //  mybatis-plus 提供了一个分页对象
        Page<SpuInfo> spuInfoPage = new Page<>(page,limit);
        //  调用服务层方法。 封装分页数据;
        IPage<SpuInfo> infoIPage = this.manageService.getSpuList(spuInfoPage,spuInfo);
        //  返回数据.
        return Result.ok(infoIPage);

    }

    //  查询销售属性：/admin/product/baseSaleAttrList
    @GetMapping("baseSaleAttrList")
    public Result getBaseSaleAttrList(){
        //  调用服务层方法
        List<BaseSaleAttr> baseSaleAttrList = this.manageService.getBaseSaleAttrList();
        //  返回数据
        return Result.ok(baseSaleAttrList);
    }

    //  /admin/product/saveSpuInfo
    @PostMapping("saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        //  调用服务层保存方法
        this.manageService.saveSpuInfo(spuInfo);
        //  返回数据
        return Result.ok();
    }

    //  http://localhost/admin/product/spuImageList/12
    @GetMapping("spuImageList/{spuId}")
    public Result getSpuImageList(@PathVariable Long spuId){
        //  调用服务层方法
        List<SpuImage> spuImageList = this.manageService.getSpuImageList(spuId);
        return Result.ok(spuImageList);
    }

    //  http://localhost/admin/product/spuSaleAttrList/12
    //  回显销售属性-销售属性值
    @GetMapping("spuSaleAttrList/{spuId}")
    public Result getSpuSaleAttrList(@PathVariable Long spuId){
        //  调用服务层方法 1：n
        List<SpuSaleAttr> spuSaleAttrList = this.manageService.getSpuSaleAttrList(spuId);
        return Result.ok(spuSaleAttrList);
    }


}