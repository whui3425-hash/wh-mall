package com.wanghui.vip.mall.goods.config;

import com.wanghui.vip.mall.goods.config.tenant.TenantWebInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置：静态资源 + 多租户请求头解析（写入 TenantContextHolder，供 MP 租户插件使用）
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final TenantWebInterceptor tenantWebInterceptor;

    public WebMvcConfig(TenantWebInterceptor tenantWebInterceptor) {
        this.tenantWebInterceptor = tenantWebInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantWebInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/images/**", "/error");
    }

    /**
     * 配置静态资源映射
     * 将 /images/** 映射到 classpath:/static/images/
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/")
                .setCachePeriod(3600); // 缓存1小时
    }
}
