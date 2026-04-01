package com.gupaoedu.vip.mall.api.filter;

import com.alibaba.fastjson.JSON;
import com.gupaoedu.vip.mall.api.permission.AuthorizationIntterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ApiFilter implements GlobalFilter, Ordered {

    @Autowired
    private AuthorizationIntterceptor authorizationIntterceptor;

    /**
     * Execute interception handling
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        // URI
        String uri = request.getURI().getPath();

        // Filter if URI is valid
        if(!authorizationIntterceptor.isInvalid(uri)){
            endProcess(exchange,404,"url bad");
            return chain.filter(exchange);
        }

        // Whether interception is required
        if(!authorizationIntterceptor.isIntercept(exchange)){
            return chain.filter(exchange);
        }

        // Token validation
        Map<String, Object> resultMap = authorizationIntterceptor.tokenIntercept(exchange);
        if(resultMap==null || !authorizationIntterceptor.rolePermission(exchange,resultMap)){
            // Token validation failed or no permission
            endProcess(exchange,401,"Access denied");
            return chain.filter(exchange);
        }

        // Directly processed by backend service
        return chain.filter(exchange);
    }

    /**
     * End process
     * @param exchange
     * @param code
     * @param message
     */
    public void endProcess(ServerWebExchange exchange,Integer code,String message){
        // Response status code 200
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("code",code);
        resultMap.put("message",message);
        exchange.getResponse().setStatusCode(HttpStatus.OK);
        exchange.getResponse().setComplete();
        exchange.getResponse().getHeaders().add("message",JSON.toJSONString(resultMap));
    }

    @Override
    public int getOrder() {
        return 10001;
    }
}
