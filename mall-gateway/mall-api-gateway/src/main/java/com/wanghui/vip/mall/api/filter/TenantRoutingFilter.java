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
 * Forces X-Tenant-Id header based on request domain (Origin/Host)
 * Overrides any client-side forged tenant ID for security
 */
@Component
public class TenantRoutingFilter implements GlobalFilter, Ordered {

    private static final String TENANT_HEADER = "X-Tenant-Id";
    private static final String DEFAULT_TENANT = "1001";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // ALWAYS resolve tenant from domain - ignore any client-provided tenant ID
        // This prevents frontend from forging tenant ID
        String tenantId = resolveTenantId(request);
        
        // Force override any existing X-Tenant-Id header with server-resolved value
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(TENANT_HEADER, tenantId)
                .build();
        
        // Continue with mutated request
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }
    
    /**
     * Resolve tenant ID based on Origin, Host, or Referer header
     * Priority: Origin > Host > Referer
     */
    private String resolveTenantId(ServerHttpRequest request) {
        // Try Origin first (most reliable for CORS requests)
        String origin = request.getHeaders().getFirst("Origin");
        if (origin != null && !origin.isEmpty()) {
            String tenant = extractTenantFromDomain(origin);
            if (tenant != null) {
                return tenant;
            }
        }
        
        // Try Host header (for direct requests)
        String host = request.getHeaders().getFirst("Host");
        if (host != null && !host.isEmpty()) {
            String tenant = extractTenantFromDomain(host);
            if (tenant != null) {
                return tenant;
            }
        }
        
        // Try Referer as fallback
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
     * shop1 -> 1001 (Tech store)
     * shop2 -> 1002 (Beauty store)
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
        // Highest precedence + 100 to ensure this runs early but after CORS
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }
}
