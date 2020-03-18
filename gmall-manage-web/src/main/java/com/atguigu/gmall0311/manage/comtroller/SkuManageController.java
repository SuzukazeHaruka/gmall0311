package com.atguigu.gmall0311.manage.comtroller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0311.bean.SkuInfo;
import com.atguigu.gmall0311.bean.SkuLsInfo;
import com.atguigu.gmall0311.service.ListService;
import com.atguigu.gmall0311.service.ManageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class SkuManageController {


    @Reference
    private ManageService manageService;

   @Reference
    private ListService listService;


    @RequestMapping("onSale")
    public String onSale(String skuId){
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        SkuLsInfo skuLsInfo = new SkuLsInfo();
        BeanUtils.copyProperties(skuInfo, skuLsInfo);
        listService.saveSkuInfo(skuLsInfo);
        return "ok";
    }




}
