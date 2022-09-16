package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * author:atGuiGu-mqx
 * date:2022/8/26 10:12
 * 描述：
 **/
@RestController // 组合注解 @ResponseBody @Controller    @ResponseBody：a.返回json 数据   b. 能将数据直接显示到页面！
@RequestMapping("admin/product/") // 表示在当前这个类中 ，有很多映射路径。所有的映射路径都是以 admin/product/
//@CrossOrigin
public class ManageController {

    @Autowired
    private ManageService manageService;

    //  http://localhost/admin/product/getCategory1
    @GetMapping("getCategory1")
    public Result getCategory1(){
        //  调用服务层  select * from base_category1 where is_deleted = 0;
        List<BaseCategory1> baseCategory1List = manageService.getCategory1();
        //  返回数据
        return Result.ok(baseCategory1List);
    }

    //  根据一级分类Id 查询二级分类数据
    //  url 路径从哪来?  http://localhost/admin/product/getCategory2/{category1Id}
    //  springmvc restful 参数的数据类型如何判断?
    @GetMapping("getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable Long category1Id){
        //  调用服务层方法
        List<BaseCategory2> baseCategory2List = this.manageService.getCategory2(category1Id);
        return Result.ok(baseCategory2List);
    }

    //  根据二级分类Id 查询三级分类数据
    @GetMapping("getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable Long category2Id){
        //  调用服务层方法
        List<BaseCategory3> baseCategory3List = this.manageService.getCategory3(category2Id);
        return Result.ok(baseCategory3List);
    }

    //  根据分类Id 查询平台属性数据
    @GetMapping("attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result getAttrInfoList(@PathVariable Long category1Id,
                                  @PathVariable Long category2Id,
                                  @PathVariable Long category3Id
                                  ){
        //  调用服务层方法: 泛型：
        List<BaseAttrInfo> baseAttrInfoList = this.manageService.getAttrInfoList(category1Id,category2Id,category3Id);
        //  返回数据；
        return Result.ok(baseAttrInfoList);

    }

    //  保存平台属性： 这个对象是谁？ 谁去接收？
    /*
        {"attrName":"test",[{"valueName":ttt},{"valueName":vvv}]}

        解决方案：
            1.  要么自定义一个实体类接收数据！
            2.  如果有直接使用！

        @RequestBody: 注解，将json 的key 直接映射到实体类的属性上！
     */
    @PostMapping("saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        //  调用服务层方法.
        this.manageService.saveAttrInfo(baseAttrInfo);
        //  返回数据
        return Result.ok();
    }

    //  /admin/product/getAttrValueList/{attrId}
    //  attrId = base_attr_info.id
    //  根据平台属性Id' 回显平台属性值集合
    @GetMapping("getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable Long attrId){
        //  调用服务层方法
        //  先根据平台属性Id 判断是否有这个属性，如果有这个属性，再获取到平台属性值集合！
        BaseAttrInfo baseAttrInfo = this.manageService.getAttrInfo(attrId);

        //  这个方法，直接根据平台属性Id 查询了平台属性值集合
        //  List<BaseAttrValue> baseAttrValueList = this.manageService.getAttrValueList(attrId);
        //  返回数据；baseAttrInfo.getAttrValueList() 非表中字段，必须要查询赋值
        return Result.ok(baseAttrInfo.getAttrValueList());

    }
}