package com.wanghui.vip.mall.user.config;

import com.wanghui.vip.mall.user.config.tenant.TenantWebInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 【Web MVC 配置】
 * 注册租户拦截器，从请求头提取 X-Tenant-Id
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private TenantWebInterceptor tenantWebInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册租户拦截器，拦截所有请求
        registry.addInterceptor(tenantWebInterceptor)
                .addPathPatterns("/**");  // 拦截所有路径
    }
}
