package com.wanghui.vip.mall.api.filter;

import com.wanghui.mall.util.JwtToken;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * JWT 认证过滤器 - B端 API 安全网关
 * 
 * 核心职责：
 * 1. 拦截所有 B 端 API 请求（放行登录接口）
 * 2. 从 Authorization Header 提取并验证 JWT Token
 * 3. 从 Token 载荷中提取 tenant_id
 * 4. 【强制】向 Header 写入 X-Tenant-Id（覆盖前端伪造值）
 * 5. 401 拦截非法请求
 */
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TENANT_HEADER = "X-Tenant-Id";
    private static final String LOGIN_PATH = "/api/permission/admin/login";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. 【必须放行】登录接口 - 否则无法获取 Token
        if (path.equals(LOGIN_PATH)) {
            return chain.filter(exchange);
        }

        // 2. 提取 Authorization Header
        String authHeader = request.getHeaders().getFirst(AUTHORIZATION_HEADER);
        
        // 3. 验证 Token 存在性
        if (authHeader == null || authHeader.isEmpty()) {
            return unauthorized(exchange, "Missing Authorization header");
        }

        // 4. 提取 Token 值（支持 Bearer 前缀）
        String token = extractToken(authHeader);
        if (token == null || token.isEmpty()) {
            return unauthorized(exchange, "Invalid token format");
        }

        // 5. 【核心】解析并验证 JWT Token
        Map<String, Object> tokenPayload;
        try {
            tokenPayload = JwtToken.parseToken(token);
        } catch (Exception e) {
            return unauthorized(exchange, "Invalid or expired token");
        }

        // 6. 验证 Token 载荷有效性
        if (tokenPayload == null || tokenPayload.isEmpty()) {
            return unauthorized(exchange, "Empty token payload");
        }

        // 7. 【核心租户注入】从 Token 中提取 tenant_id
        Object tenantIdObj = tokenPayload.get("tenantId");
        if (tenantIdObj == null) {
            return unauthorized(exchange, "Missing tenant_id in token");
        }
        String tenantId = tenantIdObj.toString();

        // 8. 【强制覆盖】使用 mutate 机制写入 X-Tenant-Id Header
        // 这会覆盖掉前端可能伪造的任何 X-Tenant-Id 值
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(TENANT_HEADER, tenantId)
                .build();

        // 9. 可选：将解析出的用户信息放入 attributes，供后续使用
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        // 10. 继续向下游转发
        return chain.filter(mutatedExchange);
    }

    /**
     * 从 Authorization Header 中提取 Token
     * 支持格式："Bearer xxxxxx" 或 "xxxxxx"
     */
    private String extractToken(String authHeader) {
        if (authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length()).trim();
        }
        return authHeader.trim();
    }

    /**
     * 返回 401 Unauthorized 响应
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");
        
        String body = String.format("{\"code\":40100,\"message\":\"%s\"}", message);
        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes()))
        );
    }

    @Override
    public int getOrder() {
        // 优先级高于 TenantRoutingFilter，确保先认证再路由
        // HIGHEST_PRECEDENCE + 50 表示在 CORS 之后，但在租户路由之前
        return Ordered.HIGHEST_PRECEDENCE + 50;
    }
}
