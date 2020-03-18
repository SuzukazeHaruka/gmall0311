package com.atguigu.gmall0311.list;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan("com.atguigu.gmall0311")
public class GmallListWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallListWebApplication.class, args);
    }

}
