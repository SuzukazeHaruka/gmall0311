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
public class BaseAttrValue implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    @TableField
    private String valueName;
    @TableField
    private String attrId;

    @TableField(exist = false)
    private String urlParam;
}
