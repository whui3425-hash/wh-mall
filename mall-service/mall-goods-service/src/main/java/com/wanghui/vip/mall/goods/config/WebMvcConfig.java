package com.wanghui.vip.mall.goods.config;

import com.wanghui.vip.mall.goods.config.tenant.TenantWebInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration for registering interceptors
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private TenantWebInterceptor tenantWebInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register tenant interceptor for all paths
        registry.addInterceptor(tenantWebInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/error",
                        "/static/**",
                        "/favicon.ico"
                );
    }
}
