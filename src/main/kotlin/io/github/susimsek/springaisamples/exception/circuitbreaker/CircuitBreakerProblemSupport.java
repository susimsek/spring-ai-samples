package io.github.susimsek.springaisamples.exception.circuitbreaker;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
@RequiredArgsConstructor
public class CircuitBreakerProblemSupport implements CircuitBreakerExceptionHandler {

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, CircuitBreakerException ex) {
        handlerExceptionResolver.resolveException(request, response, null, ex);
    }
}