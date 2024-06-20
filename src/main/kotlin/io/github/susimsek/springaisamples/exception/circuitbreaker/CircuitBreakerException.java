package io.github.susimsek.springaisamples.exception.circuitbreaker;

import lombok.Getter;

@Getter
public class CircuitBreakerException extends RuntimeException {
    
    private final String circuitBreakerName;

    public CircuitBreakerException(String circuitBreakerName, String message) {
        super(message);
        this.circuitBreakerName = circuitBreakerName;
    }

}