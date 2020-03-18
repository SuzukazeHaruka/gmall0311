package com.atguigu.gmall0311.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkuLsParams implements Serializable {

    String keyword;

    String catalog3Id;

    String[] valueId;

    int pageNo = 1;

    int pageSize = 5;
}
