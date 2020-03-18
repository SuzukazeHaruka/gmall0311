package com.atguigu.gmall0311.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0311.bean.CartInfo;
import com.atguigu.gmall0311.bean.SkuInfo;
import com.atguigu.gmall0311.cart.handle.CartCookieHandler;
import com.atguigu.gmall0311.util.LoginRequire;
import com.atguigu.gmall0311.service.CartInfoService;

import com.atguigu.gmall0311.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

    @Reference
    private CartInfoService cartInfoService;

    @Autowired
    private CartCookieHandler cartCookieHandler;

    @Reference
    private ManageService manageService;





    @RequestMapping("addToCart")
    @LoginRequire(autoRedirect = false)
    public String addCart(HttpServletRequest request, HttpServletResponse response){
        //获取userId

        String skuId = request.getParameter("skuId");
        String skuNum = request.getParameter("num");
        // 应该将对应的商品信息做一个保存
        // 调用服务层将商品数据添加到redis ，mysql
        // 获取userId
        String userId = (String) request.getAttribute("userId");
        //判断用户是否登录
        if(userId!=null){
            //说明用户已经登录
            cartInfoService.addToCart(skuId, userId, Integer.parseInt(skuNum));
        }else {
            //用户未登录
            cartCookieHandler.addToCart(request,response,skuId,userId,Integer.parseInt(skuNum));
        }
        //取得sku信息对象
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        request.setAttribute("skuInfo",skuInfo);
        request.setAttribute("num",skuNum);
        return "success";

    }

    @RequestMapping("cartList")
    @LoginRequire(autoRedirect = false)
    public  String cartList(HttpServletRequest request,HttpServletResponse response){
        String userId = (String) request.getAttribute("userId");
        //判断用户是否登录
        //已经登录从redis中取值
        List<CartInfo>cartInfoList=new ArrayList<>();
        if(userId!=null){
            //先查看未登录的购物车是否为空
            List<CartInfo> cartListCK = cartCookieHandler.getCartList(request, true);
            if(cartListCK!=null&&cartListCK.size()!=0){
                //和Redis中的数据合并
                // 合并购物车
                cartInfoList = cartInfoService.mergeToCartList(cartListCK,userId);
                // 删除未登录数据
                cartCookieHandler.deleteCartCookie(request,response);
            }else {
                cartInfoList = cartInfoService.getCartList(userId);

            }

        }else {
            cartInfoList=cartCookieHandler.getCartList(request,true);

        }
        request.setAttribute("cartList",cartInfoList);
         return "cartList";
    }

    @RequestMapping("checkCart")
    @ResponseBody
    @LoginRequire(autoRedirect = false)
    public void checkCart(HttpServletRequest request,HttpServletResponse response){
        String skuId = request.getParameter("skuId");
        String isChecked = request.getParameter("isChecked");
        String userId=(String) request.getAttribute("userId");
        if (userId!=null){
            cartInfoService.checkCart(skuId,isChecked,userId);
        }else{
            cartCookieHandler.checkCart(request,response,skuId,isChecked);
        }

    }

    @RequestMapping("toTrade")
    @LoginRequire(autoRedirect = true)
    public String toTrade(HttpServletRequest request,HttpServletResponse response){
        String userId= (String) request.getAttribute("userId");
        List<CartInfo> cartList = cartCookieHandler.getCartList(request, true);
        if(cartList!=null&&!cartList.isEmpty()){
            cartInfoService.mergeToCartList(cartList, userId);
           cartCookieHandler.deleteCartCookie(request, response);
        }

        return "redirect:http://trade.gmall.com/trade";

    }








    }
