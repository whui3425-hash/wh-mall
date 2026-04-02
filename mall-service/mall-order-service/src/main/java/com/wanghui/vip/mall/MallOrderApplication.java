package com.wanghui.vip.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan(basePackages = {"com.wanghui.vip.mall.order.mapper"})
@EnableFeignClients(basePackages = {"com.wanghui.vip.mall.goods.feign","com.wanghui.vip.mall.cart.feign"})
public class MallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallOrderApplication.class,args);
    }
}
