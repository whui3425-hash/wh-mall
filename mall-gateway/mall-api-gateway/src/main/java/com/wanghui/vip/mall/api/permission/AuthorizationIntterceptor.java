package com.wanghui.vip.mall.api.permission;

import com.gupaoedu.mall.util.JwtToken;
import com.gupaoedu.mall.util.MD5;
import com.wanghui.vip.mall.api.util.IpUtil;
import com.gupaoedu.vip.mall.permission.model.Permission;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class AuthorizationIntterceptor {

    /**
     * Check if URI is valid
     * @param uri
     * @return
     */
    public Boolean isInvalid(String uri){
        return true;
    }

    /**
     * Permission validation
     */
    public Boolean rolePermission(ServerWebExchange exchange,Map<String, Object> token){
        ServerHttpRequest request = exchange.getRequest();
        // Get URI /cart/list
        String uri = request.getURI().getPath();
        // Request method GET/POST/*
        String method = request.getMethodValue();
        // Service name
        URI routerUri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        String servicename = routerUri.getHost();

        // Match -> Get role collection
        String[] roles = token.get("roles").toString().split(",");

        Permission permission = null;
        // Check each role for permission
        for (String role : roles) {
            Set<Permission> permissions = null;

            if(permissions==null){
                continue;
            }
            // Loop check
            permission = match0(new ArrayList<Permission>(permissions), uri, method, servicename);
            if(permission!=null){
                break;
            }
        }

        // permission==null, wildcard matching
        if(permission==null){
            // Wildcard matching
        }
        return permission!=null;
    }

    /**
     * Token validation
     */
    public Map<String,Object> tokenIntercept(ServerWebExchange exchange){
        ServerHttpRequest request = exchange.getRequest();
        // Validate other addresses
        String clientIp = IpUtil.getIp(request);
        // Get token
        String token = request.getHeaders().getFirst("authorization");
        // Token validation
        Map<String, Object> resultMap = AuthorizationIntterceptor.jwtVerify(token, clientIp);
        return resultMap;
    }

    /**
     * Whether interception is required
     * true: interception required
     * false: interception not required
     */
    public Boolean isIntercept(ServerWebExchange exchange){
        ServerHttpRequest request = exchange.getRequest();
        // Get URI /cart/list
        String uri = request.getURI().getPath();
        // Request method GET/POST/*
        String method = request.getMethodValue();
        // Service name
        URI routerUri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        String servicename = routerUri.getHost();

        List<Permission> permissionsMatch0 = null;
        if(permissionsMatch0!=null){

        }
        // Wildcard matching
        Permission permission = match0(permissionsMatch0, uri, method, servicename);
        // If permission==null, execute wildcard matching
        if(permission==null){
            // Wildcard matching

            // If wildcard matching is also empty, no permission check needed
            return false;
        }
        return true;
    }


    /**
     * Matching method: exact match
     */
    public Permission match0(List<Permission> permissionsMatch0,String uri,String method,String serviceName){
        for (Permission permission : permissionsMatch0) {
            String matchUrl = permission.getUrl();
            String matchMethod = permission.getMethod();
            if(matchUrl.equals(uri)){
                // Method and service match
                if(matchMethod.equals(method) && serviceName.equals(permission.getServiceName())){
                    return permission;
                }
                if("*".equals(matchMethod) && serviceName.equals(permission.getServiceName())){
                    return permission;
                }
            }
        }
        return null;
    }

    /**
     * Token parsing
     */
    public static Map<String,Object> jwtVerify(String token,String clientIp){
        try {
            // Parse Token
            Map<String, Object> dataMap = JwtToken.parseToken(token);
            // Get MD5 of IP in Token
            String ip = dataMap.get("ip").toString();
            // Compare MD5 of IP in Token with user's IP MD5
            if(ip.equals(MD5.md5(clientIp))){
                return dataMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
