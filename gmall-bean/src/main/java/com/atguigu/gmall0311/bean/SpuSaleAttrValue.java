package com.atguigu.gmall0311.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.beans.Transient;
import java.io.Serializable;
@Data
public class SpuSaleAttrValue implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    String id ;


    String spuId;


    String saleAttrId;


    String saleAttrValueName;

    @TableField(exist = false)
    String isChecked;

}
