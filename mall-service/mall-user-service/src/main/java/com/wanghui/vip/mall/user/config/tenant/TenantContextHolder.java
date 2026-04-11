package com.wanghui.vip.mall.user.config.tenant;

/**
 * 【多租户上下文持有者】基于 ThreadLocal 实现
 * 在当前请求生命周期内存储租户 ID
 * 【重要】请求结束后必须调用 clear() 防止内存泄漏
 */
public class TenantContextHolder {

    private static final ThreadLocal<String> TENANT_CONTEXT = new ThreadLocal<>();

    private static final String DEFAULT_TENANT_ID = "1001";

    /**
     * 设置租户 ID 到当前线程上下文
     * 【调用时机】在请求拦截器中获取到 X-Tenant-Id Header 后设置
     * @param tenantId 租户标识符
     */
    public static void setTenantId(String tenantId) {
        TENANT_CONTEXT.set(tenantId);
    }

    /**
     * 从当前线程上下文获取租户 ID
     * 【调用时机】MyBatis-Plus 拦截器自动调用获取 tenant_id 值
     * @return 租户标识符，未设置时返回默认值 '1001'
     */
    public static String getTenantId() {
        String tenantId = TENANT_CONTEXT.get();
        return tenantId != null ? tenantId : DEFAULT_TENANT_ID;
    }

    /**
     * 清除当前线程上下文的租户 ID
     * 【重要】请求结束后必须调用，防止 ThreadLocal 内存泄漏
     */
    public static void clear() {
        TENANT_CONTEXT.remove();
    }

    /**
     * 检查当前线程是否设置了租户 ID
     * @return true-已设置，false-未设置
     */
    public static boolean hasTenantId() {
        return TENANT_CONTEXT.get() != null;
    }
}
