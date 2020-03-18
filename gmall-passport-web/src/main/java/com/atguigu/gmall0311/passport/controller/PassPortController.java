package com.atguigu.gmall0311.passport.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0311.bean.UserInfo;
import com.atguigu.gmall0311.passport.util.JwtUtil;
import com.atguigu.gmall0311.service.UserInfoService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServlet;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassPortController {

    @Value("${token.key}")
    String signKey;

    @Reference
    private UserInfoService userInfoService;

    @RequestMapping("index")
    public String index(HttpServletRequest request){
      /*
            用户点击登录的时候，必须从页面的某个链接访问登录模块：则登录url 后面必须有你点击的哪个链接
         */

        String originUrl = request.getParameter("originUrl");
        StringBuffer requestURL = request.getRequestURL();
        request.setAttribute("originUrl",originUrl);
        // 应该存储originUrl
        return "index";
    }


    @RequestMapping("login")
    @ResponseBody
    public String login(HttpServletRequest request , UserInfo userInfo){
        // 创建key，map，salt
        // 服务器Ip 地址 在服务器中设置 X-forwarded-for 对应的值
        String salt = request.getHeader("X-forwarded-for");
        if(userInfo!=null){
            UserInfo info = userInfoService.login(userInfo);
            if(info==null){
                return "fail";
            }else {
                //生成token
                Map<String,Object>map=new HashMap<>();
                map.put("userId",info.getId());
                map.put("nickName", info.getNickName());
                String token = JwtUtil.encode(signKey, map, salt);
                return token;
            }
        }
        return "fail";
    }

    @RequestMapping("verify")
    @ResponseBody
    public String verify(HttpServletRequest request){
        String token = request.getParameter("token");
        String salt = request.getParameter("salt");
        Map<String, Object> map = JwtUtil.decode(token, signKey, salt);
        if(map!=null&&map.size()>0){
            //获取客户id
            String userId = (String) map.get("userId");
            UserInfo userInfo = userInfoService.verify(userId);
            if(userInfo!=null){
                return "success";
            }
        }



        return "fail";
    }

}
