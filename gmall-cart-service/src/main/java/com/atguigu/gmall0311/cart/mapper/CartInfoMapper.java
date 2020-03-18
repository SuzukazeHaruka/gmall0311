package com.atguigu.gmall0311.cart.mapper;

import com.atguigu.gmall0311.bean.CartInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface CartInfoMapper extends BaseMapper<CartInfo> {
    List<CartInfo> selectCartListWithCurPrice(String userId);
}
