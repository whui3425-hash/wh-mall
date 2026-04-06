package com.wanghui.vip.mall.config;

import org.springframework.context.annotation.Configuration;

/**
 * Service dependency configuration
 * Note: MyBatis-Plus plugins are now configured in each microservice
 */
@Configuration
public class StartConfig {
    // Pagination and tenant plugins are now configured per-service
    // to avoid Bean conflicts and support SaaS multi-tenant isolation
}
