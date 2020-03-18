package com.atguigu.gmall0311.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkuLsResult implements Serializable {

    List<SkuLsInfo>skuLsInfoList;

    long total;

    long totalPages;

    List<String>attrValueIdList;

}
