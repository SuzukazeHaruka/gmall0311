package com.atguigu.gmall0311.order.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0311.bean.CartInfo;
import com.atguigu.gmall0311.bean.OrderDetail;
import com.atguigu.gmall0311.bean.OrderInfo;
import com.atguigu.gmall0311.bean.UserAddress;
import com.atguigu.gmall0311.enums.OrderStatus;
import com.atguigu.gmall0311.enums.ProcessStatus;
import com.atguigu.gmall0311.service.OrderService;
import com.atguigu.gmall0311.util.LoginRequire;
import com.atguigu.gmall0311.service.CartInfoService;
import com.atguigu.gmall0311.service.UserInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderController {

//    @Autowired
    @Reference
    private UserInfoService userInfoService;

    @Reference
    private CartInfoService cartInfoService;

    @Reference
    private OrderService orderService;


    @RequestMapping(value = "trade",method = RequestMethod.GET)
    @LoginRequire(autoRedirect = true)
    public  String tradeInit(HttpServletRequest request){



        //获取用户id
        String userId = (String) request.getAttribute("userId");
        //校验流水号
        String tradeNo = request.getParameter("tradeNo");
        boolean flag = orderService.checkTradeCode(userId, tradeNo);
        if(!flag){
            request.setAttribute("errMsg","该页面已失效，请重新结算!");
            return "tradeFail";

        }
        //得到选中的购物车列表
        List<CartInfo> cartCheckedList=cartInfoService.getCartCheckedList(userId);
        // 收货人地址
        List<UserAddress> userAddressList = userInfoService.getUserAddressByUserId(userId);
        request.setAttribute("userAddressList", userAddressList);
        //订单信息集合
        List<OrderDetail> orderDetailList=new ArrayList<>(cartCheckedList.size());
        for (CartInfo cartInfo : cartCheckedList) {
            OrderDetail orderDetail=new OrderDetail();
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            orderDetail.setOrderPrice(cartInfo.getCartPrice());
            orderDetailList.add(orderDetail);
        }

        request.setAttribute("orderDetailList",orderDetailList);
        OrderInfo orderInfo=new OrderInfo();
        orderInfo.setOrderDetailList(orderDetailList);
        orderInfo.sumTotalAmount();
        request.setAttribute("totalAmount",orderInfo.getTotalAmount());
        return  "trade";
    }




    @RequestMapping(value = "submitOrder",method = RequestMethod.POST)
    @LoginRequire
    public String submitOrder(OrderInfo orderInfo, HttpServletRequest request){
        String userId = (String) request.getAttribute("userId");


        // 初始化参数
        orderInfo.setOrderStatus(OrderStatus.UNPAID);
        orderInfo.setProcessStatus(ProcessStatus.UNPAID);
        orderInfo.sumTotalAmount();
        orderInfo.setUserId(userId);

        String orderId= orderService.saveOrder(orderInfo);
        // 获取TradeCode号
        String tradeNo = orderService.getTradeNo(userId);
        request.setAttribute("tradeCode",tradeNo);


        return "redirect://payment.gmall.com/index?orderId="+orderId;



    }




}
