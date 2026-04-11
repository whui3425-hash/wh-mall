package com.wanghui.vip.mall.user.config.tenant;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 【Web 请求拦截器】从 HTTP 请求头中提取租户 ID
 * 【核心功能】
 * 1. 从 X-Tenant-Id Header 获取租户 ID
 * 2. 存入 ThreadLocal 上下文供 MyBatis-Plus 拦截器使用
 * 3. 请求结束后清理上下文，防止内存泄漏
 */
@Component
public class TenantWebInterceptor implements HandlerInterceptor {

    private static final String TENANT_HEADER = "X-Tenant-Id";

    private static final String DEFAULT_TENANT_ID = "1001";

    /**
     * 请求预处理：提取租户 ID 并存入上下文
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantId = request.getHeader(TENANT_HEADER);

        if (tenantId == null || tenantId.isEmpty()) {
            tenantId = DEFAULT_TENANT_ID;
        }

        TenantContextHolder.setTenantId(tenantId);
        return true;
    }

    /**
     * 请求完成后清理租户上下文
     * 【重要】必须清理 ThreadLocal，防止内存泄漏
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContextHolder.clear();
    }
}
