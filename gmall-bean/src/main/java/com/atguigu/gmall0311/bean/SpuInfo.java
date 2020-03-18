package com.atguigu.gmall0311.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SpuInfo implements Serializable {



    @TableId(type = IdType.ASSIGN_ID)
    private String id;


    private String spuName;


    private String description;


    private  String catalog3Id;

    @TableField(exist = false)
    private List<SpuSaleAttr> spuSaleAttrList;
    @TableField(exist = false)
    private List<SpuImage> spuImageList;
}
