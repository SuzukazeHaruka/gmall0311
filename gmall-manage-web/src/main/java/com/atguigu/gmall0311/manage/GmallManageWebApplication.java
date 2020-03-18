package com.atguigu.gmall0311.manage;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.atguigu.gmall0311")
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class,scanBasePackages = "com.atguigu.gmall0311.manage.comtroller")
public class GmallManageWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallManageWebApplication.class, args);
    }

}
