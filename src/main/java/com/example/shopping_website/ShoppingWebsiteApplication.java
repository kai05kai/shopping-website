package com.example.shopping_website;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShoppingWebsiteApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShoppingWebsiteApplication.class, args);
        System.out.println("✅ 购物网站启动成功！访问地址：http://localhost:8080");
    }
}