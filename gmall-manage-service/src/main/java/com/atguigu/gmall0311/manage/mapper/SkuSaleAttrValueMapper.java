package com.atguigu.gmall0311.manage.mapper;

import com.atguigu.gmall0311.bean.SkuSaleAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {
    //根据spuId查询数据
    List<SkuSaleAttrValue>getSkuSaleAttrValueListBySpu(String spuId);
}
