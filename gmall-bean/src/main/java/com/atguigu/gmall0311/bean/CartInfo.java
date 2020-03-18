package com.atguigu.gmall0311.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.beans.Transient;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 购物车表 用户登录系统时更新冗余(CartInfo)实体类
 *
 * @author makejava
 * @since 2020-03-15 20:45:21
 */

@Data
public class CartInfo implements Serializable {
    private static final long serialVersionUID = -25012678555877557L;
    /**
    * 编号
    */
    @TableId(type = IdType.AUTO)
    private String id;
    /**
    * 用户id
    */
    private String userId;
    /**
    * skuid
    */
    private String skuId;
    /**
    * 放入购物车时价格
    */
    private BigDecimal cartPrice;
    /**
    * 数量
    */
    private Integer skuNum;
    /**
    * 图片文件
    */
    private String imgUrl;
    /**
    * sku名称 (冗余)
    */
    private String skuName;

    // 实时价格
    @TableField(exist = false)
    BigDecimal skuPrice;
    // 下订单的时候，商品是否勾选
    @TableField(exist = false)
    String isChecked="0";





}