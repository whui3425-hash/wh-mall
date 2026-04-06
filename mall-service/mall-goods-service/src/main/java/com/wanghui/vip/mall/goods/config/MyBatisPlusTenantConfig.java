package com.wanghui.vip.mall.goods.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.wanghui.vip.mall.goods.config.tenant.TenantContextHolder;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus multi-tenant configuration
 * Automatically appends tenant_id conditions to SQL queries
 */
@Configuration
public class MyBatisPlusTenantConfig {

    /**
     * MyBatis-Plus interceptor with tenant line and pagination inner interceptors
     * Automatically adds tenant_id filter and pagination support to all queries
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // Add tenant line interceptor for SaaS data isolation
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {

            @Override
            public Expression getTenantId() {
                String tenantId = TenantContextHolder.getTenantId();
                // Return as string value for tenant_id column
                return new StringValue(tenantId);
            }

            @Override
            public String getTenantIdColumn() {
                return "tenant_id";
            }

            @Override
            public boolean ignoreTable(String tableName) {
                // MVP stage: all tables need tenant isolation
                // Can add exceptions here for public dictionary tables
                // e.g., return "sys_config".equals(tableName) || "sys_dict".equals(tableName);
                return false;
            }
        }));

        // Add pagination interceptor for MySQL
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));

        return interceptor;
    }
}
