package com.atguigu.gmall0311.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class SpuImage implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String spuId;

    private String imgName;

    private String imgUrl;


}
