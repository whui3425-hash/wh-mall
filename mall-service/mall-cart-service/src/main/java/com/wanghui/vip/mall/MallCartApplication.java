package com.wanghui.vip.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = {"com.wanghui.vip.mall.goods.feign"})
@MapperScan(basePackages = "com.wanghui.vip.mall.cart.mapper")
public class MallCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallCartApplication.class,args);
    }
}
