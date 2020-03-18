package com.atguigu.gmall0311.item.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall0311.bean.SkuInfo;
import com.atguigu.gmall0311.bean.SkuSaleAttrValue;
import com.atguigu.gmall0311.bean.SpuSaleAttr;
import com.atguigu.gmall0311.util.LoginRequire;
import com.atguigu.gmall0311.service.ListService;
import com.atguigu.gmall0311.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;

@Controller
public class ItemController {

    @Reference
    private ManageService manageService;

    @Reference
    ListService listService;

//    @RequestMapping("{skuId}.html")
//    public String skuInfoPage(@PathVariable("skuId") String skuId){
//        return "item";
//    }


    @RequestMapping("{skuId}.html")
    @LoginRequire(autoRedirect = false)
    public String skuInfoPage(@PathVariable String skuId, Model model){
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
       // List<SkuImage> skuImageBySkuId = manageService.getSkuImageBySkuId(skuId);
       // skuInfo.setSkuImageList(skuImageBySkuId);
        //查询销售属性 销售属性集合spuId skuId
        List<SpuSaleAttr> spuSaleAttrList = manageService.getSpuSaleAttrListCheckBySku(skuInfo);
        System.out.println(spuSaleAttrList);


        listService.incrHotScore(skuId);  //最终应该由异步方式调用

        //获取销售属性值id
        List<SkuSaleAttrValue> skuSaleAttrValueList = manageService.getSkuSaleAttrValueListBySpu(skuInfo.getSpuId());
        //遍历并拼接字符串{"118|120":"33","119|122":"34","118|122":"36"}
        //for循环
        //将数据放入map中.然后将map转成json格式
        //key=118|120
        String key="";
        HashMap<String, Object>map=new HashMap<>();

        //普通循环
        for (int i = 0; i < skuSaleAttrValueList.size(); i++) {
            SkuSaleAttrValue skuSaleAttrValue=skuSaleAttrValueList.get(i);
            //什么时候停止拼接,当本次循环的skuId于下次循环的skuId不一致的时候!停止拼接.拼接到最后则停止拼接!?
            //什么时候加|
            //第一次拼接 key=118
            // 第二次拼接 key=118|
            // 第三次拼接 key=118|120 放入map 中 ，并清空key
            // 第四次拼接 key=119
            if(key.length()>0){
                key+="|";
            }
            key+=skuSaleAttrValue.getSaleAttrValueId();

            if((i+1)==skuSaleAttrValueList.size()||!skuSaleAttrValue.getSkuId().equals(skuSaleAttrValueList.get(i+1).getSkuId())){
                //放入map集合
                map.put(key,skuSaleAttrValue.getSkuId());
                //并且清空key
                key="";
            }
        }
        //将map转换成json
        String valuesSkuJson = JSON.toJSONString(map);


        model.addAttribute("valuesSkuJson",valuesSkuJson);
        model.addAttribute("spuSaleAttrList",spuSaleAttrList);
        model.addAttribute("skuInfo",skuInfo);
        return "item";
    }


}
