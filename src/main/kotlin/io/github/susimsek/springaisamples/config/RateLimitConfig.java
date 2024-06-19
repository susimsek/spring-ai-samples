package io.github.susimsek.springaisamples.config;

import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.susimsek.springaisamples.exception.ratelimit.RateLimitProblemSupport;
import io.github.susimsek.springaisamples.enums.FilterOrder;
import io.github.susimsek.springaisamples.ratelimit.RateLimitFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration(proxyBeanMethods = false)
public class RateLimitConfig {

    @Bean
    public RateLimitFilter rateLimitFilter(
        RateLimiterRegistry rateLimiterRegistry,
        RateLimitProblemSupport problemSupport,
        RequestMatchersConfig requestMatchersConfig) {
        return RateLimitFilter.builder(rateLimiterRegistry, problemSupport)
            .order(FilterOrder.RATE_LIMIT.order())
            .requestMatchers(requestMatchersConfig.staticResources()).permitAll()
            .requestMatchers(requestMatchersConfig.swaggerPaths()).permitAll()
            .requestMatchers(requestMatchersConfig.actuatorPaths()).permitAll()
            .anyRequest().rateLimited()
            .build();
    }

}