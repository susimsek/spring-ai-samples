package io.github.susimsek.springaisamples.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.susimsek.springaisamples.circuitbreaker.CircuitBreakerFilter;
import io.github.susimsek.springaisamples.exception.circuitbreaker.CircuitBreakerProblemSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration(proxyBeanMethods = false)
public class CircuitBreakerConfig {

    @Bean
    public CircuitBreakerFilter circuitBreakerFilter(
        CircuitBreakerRegistry circuitBreakerRegistry,
        CircuitBreakerProblemSupport problemSupport,
        RequestMatchersConfig requestMatchersConfig) {
        return CircuitBreakerFilter.builder(circuitBreakerRegistry, problemSupport)
            .requestMatchers(requestMatchersConfig.staticResources()).permitAll()
            .requestMatchers(requestMatchersConfig.swaggerPaths()).permitAll()
            .requestMatchers(requestMatchersConfig.actuatorPaths()).permitAll()
            .requestMatchers("/.well-known/jwks.json")
            .circuitBreakerName("jwksCircuitBreaker")
            .protectedByCircuitBreaker()
            .anyRequest()
            .protectedByCircuitBreaker()
            .build();
    }

}