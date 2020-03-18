package com.atguigu.gmall0311.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.beans.Transient;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseAttrInfo implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    @TableField
    private String attrName;
    @TableField
    private String catalog3Id;
    @TableField(exist = false)
    private List<BaseAttrValue> attrValueList;



}
