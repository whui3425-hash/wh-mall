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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * JWT 认证过滤器 - SaaS 网关安全核心
 *
 * 核心分流策略：
 * 1. 【白名单】C端公开浏览接口（商品/分类等）直接放行，无需登录
 * 2. 【黑名单】B端管理/C端交易接口（购物车/订单等）必须携带合法JWT
 * 3. 【租户注入】从JWT提取 tenant_id 和 user_id，强制覆盖前端伪造值
 */
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TENANT_HEADER = "X-Tenant-Id";
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_NAME_HEADER = "X-User-Name";
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * 【白名单】C端公开接口，无需登录即可访问
     * 包含：B端登录、C端登录、商品浏览相关接口
     */
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/api/permission/admin/login",  // B端管理员登录
            "/api/user/login",              // C端用户登录
            "/api/user/register",           // C端用户注册（验证码写死，与登录一致）
            "/api/brand/**",                // 品牌查询
            "/api/category/**",             // 分类查询
            "/api/spu/**",                  // SPU商品查询
            "/api/sku/**"                   // SKU商品查询
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // ================== 1. 白名单校验 ==================
        // C端公开浏览接口直接放行，不校验Token（TenantRoutingFilter仍会注入租户ID）
        if (isInWhiteList(path)) {
            return chain.filter(exchange);
        }

        // ================== 2. JWT 严格校验（非白名单接口）==================

        // 2.1 提取 Authorization Header
        String authHeader = request.getHeaders().getFirst(AUTHORIZATION_HEADER);

        // 2.2 验证 Token 存在性
        if (authHeader == null || authHeader.isEmpty()) {
            return unauthorized(exchange, "Missing Authorization header");
        }

        // 2.3 提取 Token 值（支持 Bearer 前缀）
        String token = extractToken(authHeader);
        if (token == null || token.isEmpty()) {
            return unauthorized(exchange, "Invalid token format");
        }

        // 2.4 【核心】解析并验证 JWT Token
        Map<String, Object> tokenPayload;
        try {
            tokenPayload = JwtToken.parseToken(token);
        } catch (Exception e) {
            return unauthorized(exchange, "Invalid or expired token");
        }

        // 2.5 验证 Token 载荷有效性
        if (tokenPayload == null || tokenPayload.isEmpty()) {
            return unauthorized(exchange, "Empty token payload");
        }

        // ================== 3. 用户身份提取 ==================
        Object userIdObj = tokenPayload.get("id");
        Object userNameObj = tokenPayload.get("username");
        if (userIdObj == null) {
            return unauthorized(exchange, "Missing user id in token");
        }
        String userId = userIdObj.toString();
        String userName = userNameObj != null ? userNameObj.toString() : "";

        // ================== 4. 【核心租户注入】====================
        Object tenantIdObj = tokenPayload.get("tenantId");
        if (tenantIdObj == null) {
            return unauthorized(exchange, "Missing tenant_id in token");
        }
        String tenantId = tenantIdObj.toString();

        // ================== 5. 【强制覆盖】Header 注入 ==================
        // 使用 mutate 机制写入 Header，覆盖前端可能伪造的任何值
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(TENANT_HEADER, tenantId)       // 租户ID（核心）
                .header(USER_ID_HEADER, userId)        // 用户ID（用于C端业务）
                .header(USER_NAME_HEADER, userName)    // 用户名（用于C端业务）
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        // 6. 继续向下游转发
        return chain.filter(mutatedExchange);
    }

    /**
     * 判断请求路径是否在白名单中
     * 支持精确匹配和 Ant 风格通配符匹配（如 /api/brand/**）
     */
    private boolean isInWhiteList(String path) {
        for (String pattern : WHITE_LIST) {
            if (matchPath(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 路径匹配算法
     * - 精确匹配：/api/permission/admin/login
     * - 通配符匹配：/api/brand/** 匹配 /api/brand/1, /api/brand/list 等
     */
    private boolean matchPath(String pattern, String path) {
        // 1. 精确匹配
        if (pattern.equals(path)) {
            return true;
        }

        // 2. 通配符匹配 /api/brand/**
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return path.startsWith(prefix);
        }

        // 3. 单级通配符 /api/brand/*
        if (pattern.endsWith("/*")) {
            String prefix = pattern.substring(0, pattern.length() - 2);
            if (path.startsWith(prefix)) {
                String remaining = path.substring(prefix.length());
                // 确保只有一级路径
                return !remaining.contains("/") || remaining.indexOf("/") == remaining.length() - 1;
            }
        }

        return false;
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
        // 优先级高于 TenantRoutingFilter (HIGHEST_PRECEDENCE + 150)
        // 确保JWT注入的Header能被后续过滤器读取
        return Ordered.HIGHEST_PRECEDENCE + 50;
    }
}
