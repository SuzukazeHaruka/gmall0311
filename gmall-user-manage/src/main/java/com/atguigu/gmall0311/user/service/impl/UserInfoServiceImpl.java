package com.atguigu.gmall0311.user.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall0311.bean.UserAddress;
import com.atguigu.gmall0311.bean.UserInfo;
import com.atguigu.gmall0311.config.RedisUtil;
import com.atguigu.gmall0311.service.UserInfoService;
import com.atguigu.gmall0311.user.mapper.UserAddressMapper;
import com.atguigu.gmall0311.user.mapper.UserInfoMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import redis.clients.jedis.Jedis;


import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    public String userKey_prefix="user:";
    public String userinfoKey_suffix=":info";
    public int userKey_timeOut=60*60*24;



    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<UserInfo> findAll() {

        return userInfoMapper.selectList(null);
    }

    @Override
    public UserInfo getUserInfoByName(String name) {

        QueryWrapper<UserInfo>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("name", name);
        return userInfoMapper.selectOne(queryWrapper);
    }

    @Override
    public List<UserInfo> getUserInfoListByName(UserInfo userInfo) {
        QueryWrapper<UserInfo>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("name", userInfo.getName());
        return userInfoMapper.selectList(queryWrapper);
    }

    @Override
    public List<UserInfo> getUserInfoListByNickName(UserInfo userInfo) {
        QueryWrapper<UserInfo>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("nick_name", userInfo.getNickName());
        return userInfoMapper.selectList(queryWrapper);
    }
    @Override
    public void addUser(UserInfo userInfo) {

        userInfoMapper.insert(userInfo);
    }

    @Override
    public void updUser(UserInfo userInfo) {
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public void delUser(UserInfo userInfo) {
        userInfoMapper.deleteById(userInfo.getId());
    }


    @Override
    public List<UserAddress> getUserAddressByUserId(String userId) {
        QueryWrapper<UserAddress>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return userAddressMapper.selectList(queryWrapper);
    }

    @Override
    public List<UserAddress> getUserAddressByUserId(UserAddress userAddress) {
        QueryWrapper<UserAddress>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id", userAddress.getUserId());
        return userAddressMapper.selectList(queryWrapper);
    }

    @Override
    public UserInfo login(UserInfo userInfo) {
        if(userInfo==null){
            return null;
        }
        String password = DigestUtils.md5DigestAsHex(userInfo.getPasswd().getBytes());
        userInfo.setPasswd(password);
        QueryWrapper<UserInfo>queryWrapper=new QueryWrapper<>();
        queryWrapper.setEntity(userInfo);
        UserInfo info = userInfoMapper.selectOne(queryWrapper);
        if (info!=null){
            //获得到redis将用户存储到redis中
            Jedis jedis = redisUtil.getJedis();
            jedis.setex(userKey_prefix+info.getId()+userinfoKey_suffix, userKey_timeOut, JSON.toJSONString(info));
            jedis.close();
            return info;
        }



        return null;
    }

    @Override
    public UserInfo verify(String userId) {
        //去缓存中查询是否有redis
        try (Jedis jedis = redisUtil.getJedis()) {
            String key =userKey_prefix+userId+userinfoKey_suffix;
            String userJson = jedis.get(key );
            //延长时效
            jedis.expire(key, userKey_timeOut);
            if(userJson!=null){
                UserInfo userInfo = JSON.parseObject(userJson, UserInfo.class);
                return userInfo;
            }
            return null;
        }

    }
}
