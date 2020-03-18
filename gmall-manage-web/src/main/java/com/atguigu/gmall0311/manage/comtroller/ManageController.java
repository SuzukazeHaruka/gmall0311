package com.atguigu.gmall0311.manage.comtroller;



import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0311.bean.*;
import com.atguigu.gmall0311.service.ManageService;



import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class ManageController {

    @Reference
    private ManageService manageService;

    @RequestMapping("getCatalog1")

    public List<BaseCatalog1>getCatalog1(){

        return manageService.getCatalog1();
    }

    // http://localhost:8082/getCatalog2?catalog1Id=2
    @RequestMapping("getCatalog2")
    public List<BaseCatalog2> getCatalog2(String catalog1Id){
        return manageService.getCatalog2(catalog1Id);
    }

    // http://localhost:8082/getCatalog2?catalog1Id=2
    @RequestMapping("getCatalog3")
    public List<BaseCatalog3> getCatalog3(String catalog2Id){
        return manageService.getCatalog3(catalog2Id);
    }

    @RequestMapping("attrInfoList")
    public List<BaseAttrInfo> attrInfoList(String catalog3Id){
        return manageService.getAttrList(catalog3Id);
    }


    @RequestMapping("getAttrValueList")
    public List<BaseAttrValue>getAttrValueList(String attrId){
        BaseAttrInfo attrInfo = manageService.getAttrInfo(attrId);

          return  attrInfo.getAttrValueList();

    }

    @RequestMapping("saveAttrInfo")
    public void saveAttrInfo(@RequestBody  BaseAttrInfo  baseAttrInfo){
            manageService.saveAttrInfo(baseAttrInfo);
    }


    @RequestMapping("index")
    public String index(){


        return "index";
    }

    @RequestMapping("baseSaleAttrList")
    public List<BaseSaleAttr>getBaseSaleAttrList(){
        return manageService.getBaseSaleAttrList();
    }





}
