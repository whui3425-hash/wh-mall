package com.wanghui.vip.mall;

import com.github.wxpay.sdk.WXPay;
import com.wanghui.vip.mall.pay.config.WeixinPayConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan(basePackages = {"com.wanghui.vip.mall.pay.mapper"})
public class MallPayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallPayApplication.class,args);
    }

    /****
     * WeChat Pay SDK object
     * @param weixinPayConfig
     * @return
     * @throws Exception
     */
    @Bean
    public WXPay wxPay(WeixinPayConfig weixinPayConfig) throws Exception {
        return new WXPay(weixinPayConfig);
    }
}
