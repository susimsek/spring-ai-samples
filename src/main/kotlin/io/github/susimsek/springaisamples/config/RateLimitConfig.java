package io.github.susimsek.springaisamples.config;

import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.susimsek.springaisamples.enums.FilterOrder;
import io.github.susimsek.springaisamples.exception.ratelimit.RateLimitProblemSupport;
import io.github.susimsek.springaisamples.ratelimit.RateLimitingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@Configuration(proxyBeanMethods = false)
@EnableAspectJAutoProxy
public class RateLimitConfig {

    @Bean
    public RateLimitingFilter rateLimitingFilter(
        RateLimiterRegistry rateLimiterRegistry,
        RateLimitProblemSupport problemSupport,
        RequestMatchersConfig requestMatchersConfig) {
        return RateLimitingFilter.builder(rateLimiterRegistry, problemSupport)
            .order(FilterOrder.RATE_LIMIT.order())
            .requestMatchers(requestMatchersConfig.staticResources()).permitAll()
            .requestMatchers(requestMatchersConfig.swaggerPaths()).permitAll()
            .requestMatchers(requestMatchersConfig.actuatorPaths()).permitAll()
            .requestMatchers("/.well-known/jwks.json")
            .rateLimiterName("jwksRateLimiter")
            .rateLimited()
            .anyRequest()
            .rateLimited()
            .build();
    }

}