package com.wanghui.vip.mall.order.config.tenant;

/**
 * Multi-tenant context holder based on ThreadLocal
 * Stores tenant ID in thread-local storage for request lifecycle
 */
public class TenantContextHolder {

    private static final ThreadLocal<String> TENANT_CONTEXT = new ThreadLocal<>();

    private static final String DEFAULT_TENANT_ID = "1001";

    /**
     * Set tenant ID to current thread context
     * @param tenantId tenant identifier
     */
    public static void setTenantId(String tenantId) {
        TENANT_CONTEXT.set(tenantId);
    }

    /**
     * Get tenant ID from current thread context
     * @return tenant identifier, returns default '1001' if not set
     */
    public static String getTenantId() {
        String tenantId = TENANT_CONTEXT.get();
        return tenantId != null ? tenantId : DEFAULT_TENANT_ID;
    }

    /**
     * Clear tenant ID from current thread context
     * Must be called after request completion to prevent memory leak
     */
    public static void clear() {
        TENANT_CONTEXT.remove();
    }

    /**
     * Check if tenant ID is set
     * @return true if tenant ID exists in context
     */
    public static boolean hasTenantId() {
        return TENANT_CONTEXT.get() != null;
    }
}
