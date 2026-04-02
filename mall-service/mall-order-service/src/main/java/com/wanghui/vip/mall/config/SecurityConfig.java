package com.wanghui.vip.mall.config;

import com.gupaoedu.mall.util.Signature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    @Value("${payconfig.aes.skey}")
    private String skey;

    @Value("${payconfig.aes.salt}")
    private String salt;

    /*****
     * Encryption and decryption tool
     * @return
     */
    @Bean
    public Signature signature(){
        return new Signature(skey,salt);
    }
}
