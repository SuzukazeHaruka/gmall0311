package com.atguigu.gmall0311.service;

import com.atguigu.gmall0311.bean.SkuLsInfo;
import com.atguigu.gmall0311.bean.SkuLsParams;
import com.atguigu.gmall0311.bean.SkuLsResult;

public interface ListService  {
    public void saveSkuInfo(SkuLsInfo skuLsInfo);

    public SkuLsResult search(SkuLsParams skuLsParams);

    public void incrHotScore(String skuId);
}
