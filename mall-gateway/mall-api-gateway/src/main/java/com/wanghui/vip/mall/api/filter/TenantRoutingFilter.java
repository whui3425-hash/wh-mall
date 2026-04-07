package com.wanghui.vip.mall.api.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global filter for SaaS multi-tenant routing
 * Injects X-Tenant-Id header based on request domain (Origin/Referer)
 */
@Component
public class TenantRoutingFilter implements GlobalFilter, Ordered {

    private static final String TENANT_HEADER = "X-Tenant-Id";
    private static final String DEFAULT_TENANT = "1001";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Try to get tenant from existing header first
        String existingTenant = request.getHeaders().getFirst(TENANT_HEADER);
        if (existingTenant != null && !existingTenant.isEmpty()) {
            // Tenant already set, proceed
            return chain.filter(exchange);
        }
        
        // Determine tenant based on Origin or Referer
        String tenantId = resolveTenantId(request);
        
        // Mutate request with tenant header
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(TENANT_HEADER, tenantId)
                .build();
        
        // Continue with mutated request
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }
    
    /**
     * Resolve tenant ID based on Origin or Referer header
     */
    private String resolveTenantId(ServerHttpRequest request) {
        // Try Origin first
        String origin = request.getHeaders().getFirst("Origin");
        if (origin != null && !origin.isEmpty()) {
            String tenant = extractTenantFromDomain(origin);
            if (tenant != null) {
                return tenant;
            }
        }
        
        // Try Referer if Origin not available
        String referer = request.getHeaders().getFirst("Referer");
        if (referer != null && !referer.isEmpty()) {
            String tenant = extractTenantFromDomain(referer);
            if (tenant != null) {
                return tenant;
            }
        }
        
        // Default fallback
        return DEFAULT_TENANT;
    }
    
    /**
     * Extract tenant ID from domain string
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
        // Highest precedence to ensure tenant is set before other filters
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
