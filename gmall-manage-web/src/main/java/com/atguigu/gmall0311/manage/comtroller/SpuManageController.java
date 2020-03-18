package com.atguigu.gmall0311.manage.comtroller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0311.bean.SkuInfo;
import com.atguigu.gmall0311.bean.SpuImage;
import com.atguigu.gmall0311.bean.SpuInfo;
import com.atguigu.gmall0311.bean.SpuSaleAttr;
import com.atguigu.gmall0311.service.ManageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class SpuManageController {

    @Reference
    private ManageService manageService;

    @RequestMapping("spuList")
    public List<SpuInfo>spuList(String catalog3Id){
        SpuInfo spuInfo=new SpuInfo();
        spuInfo.setCatalog3Id(catalog3Id);
        List<SpuInfo> spuInfoList = manageService.getSpuInfoList(spuInfo);
        return spuInfoList;
    }

    @RequestMapping("spuImageList")
    public List<SpuImage>spuImageList(String spuId){
        return manageService.getSpuImageList(spuId);
    }


    @RequestMapping("spuSaleAttrList")
    @ResponseBody
    public List<SpuSaleAttr> getspuSaleAttrList(String spuId){
        List<SpuSaleAttr> spuSaleAttrList = manageService.getSpuSaleAttrList(spuId);
        return  spuSaleAttrList;
    }

    @RequestMapping("saveSpuInfo")
    public String saveSpuInfo(@RequestBody SpuInfo spuInfo){
        manageService.saveSpuInfo(spuInfo);
        return  "OK";
    }

    @RequestMapping("saveSkuInfo")
    public String saveSpuInfo(@RequestBody SkuInfo skuInfo){
        manageService.saveSkuInfo(skuInfo);
        return "ok";
    }


}
