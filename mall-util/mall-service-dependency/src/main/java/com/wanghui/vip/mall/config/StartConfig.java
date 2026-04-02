package com.wanghui.vip.mall.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartConfig {

    /****
     * Pagination interceptor
     */
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor pageInterceptor = new PaginationInterceptor();
        // Set database type
        pageInterceptor.setDbType(DbType.MYSQL);
        return pageInterceptor;
    }
}
