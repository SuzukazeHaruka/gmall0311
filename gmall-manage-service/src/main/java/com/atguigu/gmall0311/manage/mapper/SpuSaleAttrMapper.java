package com.atguigu.gmall0311.manage.mapper;

import com.atguigu.gmall0311.bean.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {
    List<SpuSaleAttr> getSpuSaleAttrList(String spuId);

    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(String skuId, String spuId);
}
