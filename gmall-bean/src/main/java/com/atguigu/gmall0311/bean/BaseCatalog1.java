package com.atguigu.gmall0311.bean;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseCatalog1 implements Serializable {

    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private String id;

    @TableField
    private String name;



}
