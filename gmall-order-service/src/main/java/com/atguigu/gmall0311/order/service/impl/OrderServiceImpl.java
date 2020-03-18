package com.atguigu.gmall0311.order.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall0311.bean.OrderDetail;
import com.atguigu.gmall0311.bean.OrderInfo;
import com.atguigu.gmall0311.config.RedisUtil;
import com.atguigu.gmall0311.enums.OrderStatus;
import com.atguigu.gmall0311.enums.ProcessStatus;
import com.atguigu.gmall0311.order.mapper.OrderDetailMapper;
import com.atguigu.gmall0311.order.mapper.OrderInfoMapper;
import com.atguigu.gmall0311.service.OrderService;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private RedisUtil redisUtil;


    @Override
    public String saveOrder(OrderInfo orderInfo) {
        // 数据库表结构：orderInfo orderDetail
        // 总金额，订单状态，userId ,第三方交易编号，创建时间，过期时间，进程状态
        orderInfo.sumTotalAmount();
        orderInfo.setOrderStatus(OrderStatus.UNPAID);
        // 第三方交易编号
        String outTradeNo = "ATGUIGU" + System.currentTimeMillis() + "" + new Random().nextInt(1000);
        // 过期时间为下订单之后的1天
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        orderInfo.setExpireTime(calendar.getTime());
        orderInfo.setProcessStatus(ProcessStatus.UNPAID);
        // orderInfo
        orderInfoMapper.insert(orderInfo);
        // orderDetail
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        //插入订单详细信息
        if (orderDetailList != null && !orderDetailList.isEmpty()) {
            for (OrderDetail orderDetail : orderDetailList) {
                //设置订单id
                orderDetail.setOrderId(orderInfo.getId());
                orderDetailMapper.insert(orderDetail);
            }
        }


        return orderInfo.getId();
    }

    @Override
    public String getTradeNo(String userId) {
        Jedis jedis = redisUtil.getJedis();
        String tradeNoKey = "user:" + userId + ":tradeCode";
        String tradeCode = UUID.randomUUID().toString();
        jedis.setex(tradeNoKey, 10 * 60, tradeCode);
        jedis.close();
        return tradeCode;
    }

    @Override
    public boolean checkTradeCode(String userId,String tradeCodeNo){
        Jedis jedis = redisUtil.getJedis();
        String tradeNoKey = "user:" + userId + ":tradeCode";
        String tradeCode = jedis.get(tradeNoKey);
        jedis.close();
        if(tradeCode!=null&&tradeCodeNo.equals(tradeCode)){

            return true;
        }

        return false;
    }



    @Override
    public void delTradeCode(String userId){
        Jedis jedis = redisUtil.getJedis();
        String tradeNoKey = "user:" + userId + ":tradeCode";
        jedis.del(tradeNoKey);
        jedis.close();
    }

}