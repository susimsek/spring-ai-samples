package io.github.susimsek.springaisamples.exception.circuitbreaker;

import org.springframework.stereotype.Component;

@Component
public class CircuitBreakerFallbackHandler {

    public Object defaultFallback(String circuitBreakerName, Throwable t) {
        throw new CircuitBreakerException(circuitBreakerName,
            "Service unavailable. Circuit Breaker '" + circuitBreakerName + "' triggered.", t);
    }
}