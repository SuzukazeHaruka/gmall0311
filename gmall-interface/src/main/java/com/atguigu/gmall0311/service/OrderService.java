package com.atguigu.gmall0311.service;

import com.atguigu.gmall0311.bean.OrderInfo;

public interface OrderService {

    public String getTradeNo(String userId) ;

    public  String  saveOrder(OrderInfo orderInfo);

    public boolean checkTradeCode(String userId,String tradeCodeNo);

    public void delTradeCode(String userId);
}
