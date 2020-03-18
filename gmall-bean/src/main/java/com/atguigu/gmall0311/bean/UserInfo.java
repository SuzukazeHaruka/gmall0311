package com.atguigu.gmall0311.bean;

import lombok.Data;


import java.io.Serializable;

/*
一个实体类通常具备 属性，get,set
 */
@Data
public class UserInfo implements Serializable{


    private String id;

    private String loginName;

    private String nickName;

    private String passwd;

    private String name;

    private String phoneNum;

    private String email;

    private String headImg;

    private String userLevel;



}
