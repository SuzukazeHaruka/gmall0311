package com.atguigu.gmall0311.bean;

import java.beans.Transient;
import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;
import java.util.List;

import com.atguigu.gmall0311.enums.OrderStatus;
import com.atguigu.gmall0311.enums.PaymentWay;
import com.atguigu.gmall0311.enums.ProcessStatus;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import net.sf.jsqlparser.schema.Column;

/**
 * 订单表 订单表(OrderInfo)实体类
 *
 * @author makejava
 * @since 2020-03-17 15:07:53
 */
@Data
public class OrderInfo implements Serializable {
    private String id;

    private String consignee;

    private String consigneeTel;


    private BigDecimal totalAmount;

    private OrderStatus orderStatus;

    private ProcessStatus processStatus;


    private String userId;

    private PaymentWay paymentWay;

    private Date expireTime;

    private String deliveryAddress;

    private String orderComment;

    private Date createTime;

    private String parentOrderId;

    private String trackingNo;

    @TableField(exist = false)
    private List<OrderDetail> orderDetailList;


    @TableField(exist = false)
    private String wareId;


    private String outTradeNo;

    public void sumTotalAmount(){
        BigDecimal totalAmount=new BigDecimal("0");
        for (OrderDetail orderDetail : orderDetailList) {
            totalAmount= totalAmount.add(orderDetail.getOrderPrice().multiply(new BigDecimal(orderDetail.getSkuNum())));
        }
        this.totalAmount=  totalAmount;
    }

}
