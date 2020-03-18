package com.atguigu.gmall0311.manage.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall0311.bean.*;
import com.atguigu.gmall0311.config.RedisUtil;
import com.atguigu.gmall0311.manage.constant.ManageConst;
import com.atguigu.gmall0311.manage.mapper.*;
import com.atguigu.gmall0311.service.ManageService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;


import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ManageServiceImpl implements ManageService {
    @Autowired
    private BaseCatalog1Mapper baseCatalog1Mapper;
    @Autowired
    private BaseCatalog2Mapper baseCatalog2Mapper;
    @Autowired
    private BaseCatalog3Mapper baseCatalog3Mapper;
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    private SpuImageMapper spuImageMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Autowired
    private RedisUtil redisUtil;


    @Override
    public List<BaseCatalog1> getCatalog1() {
        List<BaseCatalog1> baseCatalog1s = baseCatalog1Mapper.selectList(null);
        return baseCatalog1s;
    }

    @Override
    public List<BaseCatalog2> getCatalog2(String catalog1Id) {
        QueryWrapper<BaseCatalog2> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("catalog1_id", catalog1Id);
        List<BaseCatalog2> baseCatalog2s = baseCatalog2Mapper.selectList(queryWrapper);
        return baseCatalog2s;
    }

    @Override
    public List<BaseCatalog3> getCatalog3(String catalog2Id) {
        QueryWrapper<BaseCatalog3> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("catalog2_id", catalog2Id);
        return baseCatalog3Mapper.selectList(queryWrapper);
    }

    @Override
    public List<BaseAttrInfo> getAttrList(String catalog3Id) {
       /* QueryWrapper<BaseAttrInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("catalog3_id", catalog3Id);
        List<BaseAttrInfo> infos = baseAttrInfoMapper.selectList(queryWrapper);
        infos.stream().forEach(info -> {
            String attrId = info.getId();
            QueryWrapper<BaseAttrValue> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("attr_id", attrId);
            List<BaseAttrValue> baseAttrValues = baseAttrValueMapper.selectList(queryWrapper1);
            info.setAttrValueList(baseAttrValues);
        });*/
        List<BaseAttrInfo> infos = baseAttrInfoMapper.getBaseAttrInfoListByCatalog3Id(catalog3Id);
        return infos;
    }

    @Override
    @Transactional
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        if (baseAttrInfo != null && baseAttrInfo.getId() != null) {
            baseAttrInfoMapper.updateById(baseAttrInfo);
        } else {
            baseAttrInfoMapper.insert(baseAttrInfo);
        }
        //baseAttrValue ?先清空数据在插入数据即可
        //清空数据的条件根据attrId为依据
        assert baseAttrInfo != null;
        baseAttrValueMapper.delete(new QueryWrapper<BaseAttrValue>().eq("attr_id", baseAttrInfo.getId()));
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        if (attrValueList != null && attrValueList.size() > 0) {
            for (BaseAttrValue baseAttrValue : attrValueList) {
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insert(baseAttrValue);
            }
        }


    }

    @Override
    public List<BaseAttrValue> getAttrValueList(String attrId) {
        QueryWrapper<BaseAttrValue> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_id", attrId);
        return baseAttrValueMapper.selectList(queryWrapper);
    }

    @Override
    public BaseAttrInfo getAttrInfo(String attrId) {

        return baseAttrInfoMapper.selectById(attrId);
    }

    @Override
    public List<SpuInfo> getSpuInfoList(SpuInfo spuInfo) {
        QueryWrapper<SpuInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("catalog3_id", spuInfo.getCatalog3Id());
        List<SpuInfo> spuInfos = spuInfoMapper.selectList(queryWrapper);
        return spuInfos;

    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {

        return baseSaleAttrMapper.selectList(null);
    }

    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        //什么情况下保存，什么情况下更新
        if (spuInfo.getId() == null || spuInfo.getId().length() == 0) {
            //保存数据
            spuInfo.setId(null);
            spuInfoMapper.insert(spuInfo);
        } else {
            spuInfoMapper.updateById(spuInfo);
        }
        //spuImage图片列表 先删除 ，在新增

        spuImageMapper.deleteById(spuInfo.getId());

        //保存数据，先获取数据
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if (spuImageList != null && spuImageList.size() > 0) {
            spuImageList.stream().forEach(spuImage -> {
                spuImage.setId(null);
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insert(spuImage);
            });
        }

        //销售属性 删除 插入
        spuSaleAttrMapper.deleteById(spuInfo.getId());

        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();

        if (spuSaleAttrList != null && spuSaleAttrList.size() > 0) {
            spuSaleAttrList.stream().forEach(spuSaleAttr -> {
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insert(spuSaleAttr);
                //添加销售属性值
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if (spuSaleAttrValueList != null && spuSaleAttrValueList.size() > 0) {
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        spuSaleAttrValue.setId(null);
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValue.setSaleAttrId(spuSaleAttr.getId());
                    }
                }
            });

        }

    }

    @Override
    public List<SpuImage> getSpuImageList(String spuId) {
        QueryWrapper<SpuImage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("spu_id", spuId);
        List<SpuImage> spuImages = spuImageMapper.selectList(queryWrapper);
        return spuImages;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId) {
        return spuSaleAttrMapper.getSpuSaleAttrList(spuId);
    }

    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        if (skuInfo.getId() == null || skuInfo.getId().length() == 0) {
            skuInfo.setId(null);
            skuInfoMapper.insert(skuInfo);
        } else {
            skuInfoMapper.updateById(skuInfo);
        }
        //sku image
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (skuImageList != null && skuImageList.size() > 0) {
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(skuInfo.getId());
                skuImageMapper.insert(skuImage);
            }
        }
        //skuAttrValue
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (skuAttrValueList != null && skuAttrValueList.size() > 0) {
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuInfo.getId());
                skuAttrValueMapper.insert(skuAttrValue);
            }
        }
        //skuSaleAttr
        List<SkuSaleAttrValue> skuSaleAttrValues = skuInfo.getSkuSaleAttrValueList();
        if (skuSaleAttrValues != null && skuAttrValueList.size() > 0) {
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValues) {
                skuSaleAttrValue.setSkuId(skuInfo.getId());
                skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            }
        }

    }

    @Override
    public SkuInfo getSkuInfo(String skuId) {


           /* Jedis jedis=redisUtil.getJedis();
            jedis.set("text","text_value");*/
        return getSkuInfoRedisson(skuId);


    }

    private SkuInfo getSkuInfoRedisson(String skuId) {
        //放入业务逻辑代码
        SkuInfo skuInfo = null;
        Jedis jedis = null;
        RLock lock = null;
        try {
            String skuInfoKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKUKEY_SUFFIX;
            jedis = redisUtil.getJedis();
            if (!jedis.exists(skuInfoKey)) {
                Config config = new Config();
                config.useSingleServer().setAddress("redis://112.74.172.1:6379");
                RedissonClient redissonClient = Redisson.create(config);
                // 使用redisson 调用getLock
                lock = redissonClient.getLock("yourLock");
                lock.lock(10, TimeUnit.SECONDS);
                System.out.println("没有命中缓存");
                skuInfo = getSkuInfoDB(skuId);
                jedis.setex(skuInfoKey, ManageConst.SKUKEY_TIMEOUT, JSON.toJSONString(skuInfo));
                return skuInfo;

            } else {
                System.out.println("等待！");
                // 等待
             //   Thread.sleep(1000);
                // 自旋
                String skuRedisStr = jedis.get(skuInfoKey);
                skuInfo = JSON.parseObject(skuRedisStr, SkuInfo.class);
                return skuInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            if(lock!=null){
                lock.unlock();
            }
        }
        return getSkuInfoDB(skuId);
    }


    private SkuInfo getSkuInfoJedis(String skuId) {
        SkuInfo skuInfo = null;
        Jedis jedis = null;
        try {
            String skuInfoKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKUKEY_SUFFIX;
            jedis = redisUtil.getJedis();
            if (!jedis.exists(skuInfoKey)) {
                //没有数据,需要加锁,去玩数据,还要放入缓存中,下次直接从缓存中取得即可!
                String skuLockKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKULOCK_SUFFIX;
                //生成锁
                System.out.println("没有命中缓存");
                String lockKey = jedis.set(skuLockKey, "OK", "NX", "PX", ManageConst.SKULOCK_EXPIRE_PX);
                if ("OK".equals(lockKey)) {
                    //获取锁
                    System.out.println("获取锁");
                    skuInfo = getSkuInfoDB(skuId);
                    //将数据转为json字符串存入缓存
                    String skuRedisStr = JSON.toJSONString(skuInfo);
                    jedis.setex(skuInfoKey, ManageConst.SKUKEY_TIMEOUT, skuRedisStr);
                    return skuInfo;
                }

            } else {
                System.out.println("等待！");
                // 等待
                Thread.sleep(1000);
                // 自旋
                String skuRedisStr = jedis.get(skuInfoKey);
                skuInfo = JSON.parseObject(skuRedisStr, SkuInfo.class);
                return skuInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return getSkuInfoDB(skuId);
    }

    SkuInfo getSkuInfoDB(String skuId) {
        SkuInfo skuInfo;
        skuInfo = skuInfoMapper.selectById(skuId);
        skuInfo.setSkuImageList(getSkuImageBySkuId(skuId));
        //查询属性值
        SkuSaleAttrValue saleAttrValue=new SkuSaleAttrValue();
        saleAttrValue.setSkuId(skuId);
        QueryWrapper<SkuSaleAttrValue>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("sku_id", skuId);
        List<SkuSaleAttrValue> saleAttrValues = skuSaleAttrValueMapper.selectList(queryWrapper);
        skuInfo.setSkuSaleAttrValueList(saleAttrValues);
        //查询平台属性值集合
        QueryWrapper<SkuAttrValue>attrValueQueryWrapper=new QueryWrapper<>();
        attrValueQueryWrapper.eq("sku_id", skuId);
        List<SkuAttrValue> skuAttrValues = skuAttrValueMapper.selectList(attrValueQueryWrapper);
        skuInfo.setSkuAttrValueList(skuAttrValues);

        return skuInfo;
    }

    @Override
    public List<SkuImage> getSkuImageBySkuId(String skuId) {
        QueryWrapper<SkuImage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sku_id", skuId);
        List<SkuImage> skuImages = skuImageMapper.selectList(queryWrapper);
        return skuImages;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(SkuInfo skuInfo) {
        return spuSaleAttrMapper.getSpuSaleAttrListCheckBySku(skuInfo.getId(), skuInfo.getSpuId());
    }

    @Override
    public List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId) {

        // 根据spuId 查询数据
        return skuSaleAttrValueMapper.getSkuSaleAttrValueListBySpu(spuId);

    }

    @Override
    public List<BaseAttrInfo> getAttrList(List<String> attrValueIdList) {
        String valueIds= StringUtils.join(attrValueIdList.toArray(), ",");
        List<BaseAttrInfo> baseAttrInfoList= baseAttrInfoMapper.selectAttrInfoListByIds(valueIds);

        return baseAttrInfoList;
    }

}