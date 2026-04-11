package com.wanghui.vip.mall.user.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.wanghui.vip.mall.user.config.tenant.TenantContextHolder;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 多租户配置
 * 【核心功能】自动为所有 SQL 添加 tenant_id 条件实现数据隔离
 * 【扩展功能】支持全局表绕过租户拦截（如 address 地址表）
 */
@Configuration
public class MyBatisPlusTenantConfig {

    /**
     * MyBatis-Plus 拦截器配置
     * 1. 多租户拦截器：自动追加 tenant_id 过滤条件
     * 2. 分页拦截器：支持 MySQL 分页查询
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 【多租户拦截器】SaaS 数据隔离核心实现
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {

            /**
             * 获取当前租户 ID（从 ThreadLocal 上下文获取）
             * @return 租户 ID 表达式
             */
            @Override
            public Expression getTenantId() {
                String tenantId = TenantContextHolder.getTenantId();
                // 返回字符串值作为 tenant_id 列的值
                return new StringValue(tenantId);
            }

            /**
             * 指定租户 ID 列名
             * @return 列名
             */
            @Override
            public String getTenantIdColumn() {
                return "tenant_id";
            }

            /**
             * 【核心】判断表是否需要忽略租户拦截
             * 【场景】全局表（如地址库、字典表）没有 tenant_id 字段，需要绕过拦截
             *
             * @param tableName 表名
             * @return true-忽略租户拦截，false-进行租户拦截（默认）
             */
            @Override
            public boolean ignoreTable(String tableName) {
                // 【全局表】address 表是全局地址库，没有 tenant_id 字段，绕过租户拦截
                if ("address".equals(tableName)) {
                    return true;
                }
                // 其他表继续走租户拦截
                return false;
            }
        }));

        // 【分页拦截器】支持 MySQL 分页
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));

        return interceptor;
    }
}
