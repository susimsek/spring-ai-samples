package io.github.susimsek.springaisamples.config;

import io.github.susimsek.springaisamples.enums.FilterOrder;
import io.github.susimsek.springaisamples.exception.idempotency.IdempotencyProblemSupport;
import io.github.susimsek.springaisamples.idempotency.IdempotencyFilter;
import io.github.susimsek.springaisamples.service.IdempotencyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdempotencyConfig {

    @Bean
    public IdempotencyFilter idempotencyFilter(
        IdempotencyService idempotencyService,
        IdempotencyProblemSupport problemSupport,
        RequestMatchersConfig requestMatchersConfig) {
        return IdempotencyFilter.builder(idempotencyService, problemSupport)
            .order(FilterOrder.IDEMPOTENCY.order())
            .requestMatchers(requestMatchersConfig.staticResources()).permitAll()
            .requestMatchers(requestMatchersConfig.swaggerPaths()).permitAll()
            .requestMatchers(requestMatchersConfig.actuatorPaths()).permitAll()
            .requestMatchers(requestMatchersConfig.actuatorPaths()).permitAll()
            .requestMatchers("/api/security/sign").idempotent()
            .anyRequest().permitAll()
            .build();
    }
}