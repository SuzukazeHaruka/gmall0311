package com.atguigu.gmall0311.manage;




import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.atguigu.gmall0311")
@SpringBootApplication
@MapperScan(basePackages = "com.atguigu.gmall0311.manage.mapper")

public class GmallManageServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GmallManageServiceApplication.class,args);
    }
}
