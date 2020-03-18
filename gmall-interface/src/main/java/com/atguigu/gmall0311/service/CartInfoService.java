package com.atguigu.gmall0311.service;

import com.atguigu.gmall0311.bean.CartInfo;

import java.util.List;

public interface CartInfoService {
    public  void  addToCart(String skuId,String userId,Integer skuNum);

    List<CartInfo> getCartList(String userId);

    // 缓存中没有数据，则从 数据库中加载
    public List<CartInfo> loadCartCache(String userId);

    List<CartInfo> mergeToCartList(List<CartInfo> cartListCK, String userId);

    void checkCart(String skuId, String isChecked, String userId);

    List<CartInfo> getCartCheckedList(String userId);
}
