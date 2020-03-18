package com.atguigu.gmall0311.cart.handle;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall0311.bean.CartInfo;
import com.atguigu.gmall0311.bean.SkuInfo;
import com.atguigu.gmall0311.config.CookieUtil;
import com.atguigu.gmall0311.service.ManageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Component
@Slf4j
public class CartCookieHandler  {

    // 定义购物车名称
    private String cookieCartName = "CART";
    // 设置cookie 过期时间
    private int COOKIE_CART_MAXAGE=7*24*3600;

    @Reference
    private ManageService manageService;



    public void addToCart(HttpServletRequest request, HttpServletResponse response, String skuId, String userId, int skuNum) {
        //cookie中是否有购物车存在,有可能有中文,所以要进行序列化
       //判断cookie中是否有购物车 有可能有中文，所有要进行序列化
        /*
            1.  判断购物车中是否有该商品 通过skuId 去cookie 中循环比较
            2.  有： 数量相加
            3.  没有：直接添加到cookie
         */
        // 获取购物车中的所有数据
        // 应该很多条数据
        String cookieValue = CookieUtil.getCookieValue(request, cookieCartName, true);
        List<CartInfo> cartInfoList = new ArrayList<>();
        boolean ifExist=false;
        //遍历找出购物车相符的物品进行添加
        if (StringUtils.isNotEmpty(cookieValue)){
            cartInfoList = JSON.parseArray(cookieValue, CartInfo.class);
            // 通过skuId 去cookie 中循环比较
            for (CartInfo cartInfo : cartInfoList) {
                if (cartInfo.getSkuId().equals(skuId)){
                    // 数量相加
                    cartInfo.setSkuNum(cartInfo.getSkuNum()+skuNum);
                    cartInfo.setSkuPrice(cartInfo.getCartPrice());
                    ifExist = true;
                }
            }
        }
        //购物车里没有对应的商品或者没有购物车
        if(!ifExist){
            SkuInfo skuInfo = manageService.getSkuInfo(skuId);
            CartInfo cartInfo=new CartInfo();

            cartInfo.setSkuId(skuId);
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setSkuPrice(skuInfo.getPrice());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());

            cartInfo.setUserId(userId);
            cartInfo.setSkuNum(skuNum);
            cartInfoList.add(cartInfo);

        }
        //把购物车写入cookie
        String newJsonCart = JSON.toJSONString(cartInfoList);
        CookieUtil.setCookie(request, response, cookieCartName, newJsonCart, COOKIE_CART_MAXAGE, true);




    }

    public List<CartInfo> getCartList(HttpServletRequest request, Boolean isDecoder) {
        String jsonCart = CookieUtil.getCookieValue(request, cookieCartName, isDecoder);
        List<CartInfo> cartInfoList = JSON.parseArray(jsonCart, CartInfo.class);
        return cartInfoList;
    }

    public void deleteCartCookie(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request,response,cookieCartName);
    }

    public void checkCart(HttpServletRequest request, HttpServletResponse response, String skuId, String isChecked) {
        List<CartInfo> cartList = getCartList(request, true);
         cartList.stream()
                 .filter(cartInfo -> cartInfo
                 .getSkuId().equals(skuId))
                 .collect(Collectors.toList())
                 .forEach(cartInfo -> cartInfo.setIsChecked(isChecked));
        String newCartJson = JSON.toJSONString(cartList);
        CookieUtil.setCookie(request, response, cookieCartName, newCartJson, COOKIE_CART_MAXAGE, true);

    }
}
