package com.wanghui.vip.mall.api.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 多租户路由过滤器 - 作为 JWT 过滤器的降级兜底
 * 
 * 逻辑变更：
 * 1. 优先检查是否已有 X-Tenant-Id（由 JwtAuthenticationFilter 注入）
 * 2. 如果 JWT 过滤器已注入，则直接放行（不覆盖）
 * 3. 仅在没有 X-Tenant-Id 时，才从域名解析租户 ID
 * 
 * 执行顺序：在 JwtAuthenticationFilter 之后（Ordered + 150）
 */
@Component
public class TenantRoutingFilter implements GlobalFilter, Ordered {

    private static final String TENANT_HEADER = "X-Tenant-Id";
    private static final String DEFAULT_TENANT = "1001";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // 【关键逻辑】检查是否已有 X-Tenant-Id（来自 JWT Token）
        String existingTenantId = request.getHeaders().getFirst(TENANT_HEADER);
        
        if (existingTenantId != null && !existingTenantId.isEmpty()) {
            // JWT 过滤器已注入 tenant_id，直接放行（不覆盖）
            // 这保证了 Token 中的租户信息优先级最高
            return chain.filter(exchange);
        }
        
        // 没有 X-Tenant-Id 时，才从域名解析（降级兜底）
        String tenantId = resolveTenantId(request);
        
        // 注入解析的租户 ID
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(TENANT_HEADER, tenantId)
                .build();
        
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }
    
    /**
     * 基于域名解析租户 ID（降级策略）
     */
    private String resolveTenantId(ServerHttpRequest request) {
        // Try Origin first
        String origin = request.getHeaders().getFirst("Origin");
        if (origin != null && !origin.isEmpty()) {
            String tenant = extractTenantFromDomain(origin);
            if (tenant != null) return tenant;
        }
        
        // Try Host header
        String host = request.getHeaders().getFirst("Host");
        if (host != null && !host.isEmpty()) {
            String tenant = extractTenantFromDomain(host);
            if (tenant != null) return tenant;
        }
        
        // Try Referer
        String referer = request.getHeaders().getFirst("Referer");
        if (referer != null && !referer.isEmpty()) {
            String tenant = extractTenantFromDomain(referer);
            if (tenant != null) return tenant;
        }
        
        return DEFAULT_TENANT;
    }
    
    /**
     * 从域名提取租户 ID
     */
    private String extractTenantFromDomain(String domain) {
        String lowerDomain = domain.toLowerCase();
        if (lowerDomain.contains("shop1")) {
            return "1001";
        } else if (lowerDomain.contains("shop2")) {
            return "1002";
        }
        return null;
    }

    @Override
    public int getOrder() {
        // 在 JwtAuthenticationFilter (HIGHEST_PRECEDENCE + 50) 之后执行
        // 这样 JWT 注入的 tenant_id 会优先被保留
        return Ordered.HIGHEST_PRECEDENCE + 150;
    }
}
