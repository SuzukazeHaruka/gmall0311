package com.atguigu.gmall0311.bean;

import lombok.Data;


import java.io.Serializable;


@Data
public class UserAddress implements Serializable {


    private String id;

    private String userAddress;

    private String userId;

    private String consignee;

    private String phoneNum;

    private String isDefault;

}
