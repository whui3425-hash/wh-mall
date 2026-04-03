package com.wanghui.vip.mall.api.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.result.view.ViewResolver;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class GatewayConfiguration {

    private final List<ViewResolver> viewResolvers;
    private final ServerCodecConfigurer serverCodecConfigurer;

    public GatewayConfiguration(ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                ServerCodecConfigurer serverCodecConfigurer) {
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    /**
     * Circuit breaker degradation exception handling
     * @return
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
        // Register the block exception handler for Spring Cloud Gateway.
        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    /**
     * Get current Route and process according to Sentinel flow control rules
     * @return
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public GlobalFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }

    /**
     * Global CORS configuration for frontend SaaS access
     * Allows all origins, methods, headers including X-Tenant-Id
     * @return
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        // Allow all origins
        corsConfig.addAllowedOrigin("*");
        // Allow all methods
        corsConfig.addAllowedMethod("*");
        // Allow all headers including X-Tenant-Id
        corsConfig.addAllowedHeader("*");
        // Allow credentials (cookies)
        corsConfig.setAllowCredentials(true);
        // Max age for preflight cache
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }


    /**
     * Rules and API loading
     */
    @PostConstruct
    public void doInit(){
        initCustomizedApis();
        initGatewayRules();
    }

    /**
     * Define API groups
     */
    private void initCustomizedApis(){
        // Define collection to store API groups
        Set<ApiDefinition> definitions = new HashSet<ApiDefinition>();

        // Create each API and configure rules
        ApiDefinition cartApi = new ApiDefinition("mall_cart_api")
                .setPredicateItems(new HashSet<ApiPredicateItem>(){{
                        // /cart/list
                        add(new ApiPathPredicateItem().setPattern("/cart/list"));
                        // /cart/*/*
                        add(new ApiPathPredicateItem().setPattern("/cart/**")
                        // Match by prefix
                        .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
                }});

        // Add created API to collection
        definitions.add(cartApi);
        // Manually load API to Sentinel
        GatewayApiDefinitionManager.loadApiDefinitions(definitions);
    }


    /**
     * Rate limiting rule definition
     */
    public void initGatewayRules(){
        // Create collection to store all rules
        Set<GatewayFlowRule> rules = new HashSet<GatewayFlowRule>();

        // Create new rule and add to collection
        rules.add(new GatewayFlowRule("goods_route")
                // Request threshold
                .setCount(6)
                // Burst flow additional allowed concurrency
                .setBurst(2)
                // Rate limiting behavior
                // CONTROL_BEHAVIOR_RATE_LIMITER: uniform queuing
                // CONTROL_BEHAVIOR_DEFAULT: direct failure
                // Statistical time window, unit: seconds, default 1 second
                .setIntervalSec(30));

        // Create new rule and add to collection
        rules.add(new GatewayFlowRule("mall_cart_api")
                // Request threshold
                .setCount(2)
                // Statistical time window, unit: seconds, default 1 second
                .setIntervalSec(2));
        // Manually load rule configuration
        GatewayRuleManager.loadRules(rules);
    }
}