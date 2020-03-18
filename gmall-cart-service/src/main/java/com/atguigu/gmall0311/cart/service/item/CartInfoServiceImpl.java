package com.atguigu.gmall0311.cart.service.item;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall0311.bean.CartInfo;
import com.atguigu.gmall0311.bean.SkuInfo;
import com.atguigu.gmall0311.cart.constant.CartConst;
import com.atguigu.gmall0311.cart.mapper.CartInfoMapper;
import com.atguigu.gmall0311.config.RedisUtil;
import com.atguigu.gmall0311.service.CartInfoService;
import com.atguigu.gmall0311.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alibaba.fastjson.JSON.*;

@Service
public class CartInfoServiceImpl implements CartInfoService {

    @Autowired
    private CartInfoMapper cartInfoMapper;

    @Reference
    private ManageService manageService;

    @Autowired
    private RedisUtil redisUtil;


    @Override
    public void addToCart(String skuId, String userId, Integer skuNum) {
        //先检查cart中是否有
        CartInfo cartInfo=new CartInfo();
        cartInfo.setSkuId(skuId);
        cartInfo.setUserId(userId);
        QueryWrapper<CartInfo>queryWrapper=new QueryWrapper<>();
        queryWrapper.setEntity(cartInfo);
        CartInfo cartInfoExist = cartInfoMapper.selectOne(queryWrapper);
        if(cartInfoExist!=null){
            cartInfoExist.setSkuNum(skuNum+cartInfoExist.getSkuNum());
            //给实时价格赋值
            cartInfoExist.setCartPrice(cartInfoExist.getCartPrice());
            cartInfoMapper.updateById(cartInfoExist);
        }else {
            //如果不存在保存到购物车
            SkuInfo skuInfo = manageService.getSkuInfo(skuId);
            CartInfo cartInfo1=new CartInfo();
            cartInfo1.setCartPrice(skuInfo.getPrice());
            cartInfo1.setSkuNum(skuNum);
            cartInfo1.setSkuId(skuId);
            cartInfo1.setSkuName(skuInfo.getSkuName());
            cartInfo1.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo1.setUserId(userId);
            cartInfo1.setSkuPrice(skuInfo.getPrice());
            cartInfoMapper.insert(cartInfo1);
            cartInfoExist = cartInfo1;
        }
        //构建key user:userId:cart
        String key= CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;
        Jedis jedis = redisUtil.getJedis();
        String jsonCartInfo = toJSONString(cartInfoExist);
        Long ttl = jedis.ttl(jsonCartInfo);
        jedis.expire(jsonCartInfo, ttl.intValue());
        jedis.close();

    }

    @Override
    public List<CartInfo> getCartList(String userId) {
        Jedis jedis = redisUtil.getJedis();
        String key= CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;
        List<String> hvals = jedis.hvals(key);
        if(hvals!=null&&hvals.size()>0){
            List<CartInfo>cartInfoList=new ArrayList<>();
            for (String hval : hvals) {
                CartInfo cartInfo = parseObject(hval, CartInfo.class);
                cartInfoList.add(cartInfo);
            }
            //查询的时候按照更新时间排序
            cartInfoList.sort((o1,o2)->{
                return o1.getId().compareTo(o2.getId());
            });
            jedis.close();
            return cartInfoList;
        }else {
            //从数据库获取数据
            List<CartInfo> cartInfoList = loadCartCache(userId);
            jedis.close();
            return cartInfoList;
        }



    }

    @Override
    public List<CartInfo> loadCartCache(String userId) {
        /*
        1.  根据userId 查询一下当前商品的实时价格：
            cartInfo.skuPrice = skuInfo.price
        2.  将查询出来的数据集合放入缓存！
         */
        List<CartInfo> cartInfoList = cartInfoMapper.selectCartListWithCurPrice(userId);
        if(cartInfoList==null&&cartInfoList.size()==0){
            return null;
        }
        String userCartKey=CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;
        Map<String,String>map=new HashMap<>();
        for (CartInfo cartInfo : cartInfoList) {
            map.put(cartInfo.getSkuId(), JSON.toJSONString(cartInfo));
        }
        //将map放入缓存
        Jedis jedis = redisUtil.getJedis();
        // 将map 放入缓存
        jedis.hmset(userCartKey, map);
        // hgetAll -- map
        jedis.close();
        return cartInfoList;
    }

    @Override
    public List<CartInfo> mergeToCartList(List<CartInfo> cartListCK, String userId) {
        //重新从数据库中获取数据

        List<CartInfo> cartInfoList = loadCartCache(userId);
        Jedis jedis = redisUtil.getJedis();
        String userCartKey=CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;
        // 获取数据库中的数据
        List<CartInfo> cartInfoListDB = cartInfoMapper.selectCartListWithCurPrice(userId);
        for (CartInfo cartInfoCK : cartListCK) {
            // 合并条件 skuId 相同的时候合并
            boolean isMatch=false;
            for (CartInfo cartInfoDB : cartInfoListDB) {
                if(cartInfoCK.getSkuId().equals(cartInfoDB.getSkuId())){
                    cartInfoDB.setSkuNum(cartInfoCK.getSkuNum()+cartInfoDB.getSkuNum());

                    //更新
                    cartInfoMapper.updateById(cartInfoDB);
                    //删除cookie中的

                    isMatch=true;
                }
            }
            //未登录的数据再数据库中没有,那么就直接插入
            if (!isMatch){
                // 未登录的时候的userId 为null
                cartInfoCK.setUserId(userId);
                cartInfoMapper.insert(cartInfoCK);
            }


        }
        // 最后再查询一次更新之后，新添加的所有数据
        cartInfoList = loadCartCache(userId);
        cartInfoList.forEach(cartInfo -> {
           cartListCK.forEach(cartInfoCk->{
               if(cartInfo.getSkuId().equals(cartInfoCk.getSkuId())){
                   if("1".equals(cartInfoCk.getIsChecked())){
                       cartInfo.setIsChecked(cartInfoCk.getIsChecked());
                   }
               }
           });
        });
        return cartInfoList;



    }

    @Override
    public void checkCart(String skuId, String isChecked, String userId) {
        String userCartKey=CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;
        Jedis jedis = redisUtil.getJedis();
        String cartJson = jedis.hget(userCartKey, skuId);
        CartInfo cartInfo = parseObject(cartJson, CartInfo.class);
        cartInfo.setIsChecked(isChecked);
        String cartCheckedJson = JSON.toJSONString(cartInfo);
        jedis.hset(userCartKey,skuId,cartCheckedJson);
        //新增到已选择的购物车
        String userCheckedKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CHECKED_KEY_SUFFIX;
        if(isChecked.equals("1")){
            jedis.hset(userCheckedKey, skuId, cartCheckedJson);
        }else {
            jedis.hdel(userCheckedKey, skuId);
        }
        jedis.close();

    }

    @Override
    public List<CartInfo> getCartCheckedList(String userId) {
        //获得redis中的check列表
        Jedis jedis = redisUtil.getJedis();
        String userCheckedKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CHECKED_KEY_SUFFIX;

        List<String> cartCheckedList  = jedis.hvals(userCheckedKey);
        List<CartInfo> newCartList = new ArrayList<>();
        for (String cartJson  : cartCheckedList) {
            CartInfo cartInfo = JSON.parseObject(cartJson,CartInfo.class);
            newCartList.add(cartInfo);
        }
        return newCartList;
    }
}
