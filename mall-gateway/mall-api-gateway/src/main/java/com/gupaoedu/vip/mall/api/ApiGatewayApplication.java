package com.gupaoedu.vip.mall.api;

import com.gupaoedu.vip.mall.api.limit.IpKeyResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class,args);
    }

    /**
     * IP rate limiting
     * @return
     */
    @Bean("ipKeyResolver")
    public KeyResolver ipKeyResolver(){
        return new IpKeyResolver();
    }
}
