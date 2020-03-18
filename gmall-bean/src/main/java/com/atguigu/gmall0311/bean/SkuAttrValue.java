package com.atguigu.gmall0311.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
@Data
public class SkuAttrValue implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    String id;


    String attrId;


    String valueId;


    String skuId;

}
