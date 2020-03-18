package com.atguigu.gmall0311.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
@Data
public class SkuInfo implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    String id;


    String spuId;


    BigDecimal price;


    String skuName;


    BigDecimal weight;


    String skuDesc;


    String catalog3Id;


    String skuDefaultImg;

    @TableField(exist = false)
    List<SkuImage> skuImageList;

    @TableField(exist = false)
    List<SkuAttrValue> skuAttrValueList;

    @TableField(exist = false)
    List<SkuSaleAttrValue> skuSaleAttrValueList;

}
