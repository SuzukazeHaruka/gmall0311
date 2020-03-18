package com.atguigu.gmall0311.bean;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
/**
 * 订单明细表(OrderDetail)实体类
 *
 * @author makejava
 * @since 2020-03-17 15:20:54
 */
@Data
public class OrderDetail implements Serializable {
    private static final long serialVersionUID = 135670175778726702L;
    /**
    * 编号
    */
    private String  id;
    /**
    * 订单编号
    */
    private String  orderId;
    /**
    * sku_id
    */
    private String  skuId;
    /**
    * sku名称（冗余)
    */
    private String skuName;
    /**
    * 图片名称（冗余)
    */
    private String imgUrl;
    /**
    * 购买价格(下单时sku价格）
    */
    private BigDecimal orderPrice;
    /**
    * 购买个数
    */
    private Integer  skuNum;
    /**
     * 是否还有库存
     */
    @TableField(exist = false)
    private Integer hasStock;

}