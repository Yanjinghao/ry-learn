package com.ry.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@MapperScan("com.ry.shop.mapper")
@SpringBootApplication
public class Week1ShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(Week1ShopApplication.class, args);
    }
}
