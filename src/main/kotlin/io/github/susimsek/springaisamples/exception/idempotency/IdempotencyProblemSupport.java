package io.github.susimsek.springaisamples.exception.idempotency;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
@RequiredArgsConstructor
public class IdempotencyProblemSupport implements IdempotencyExceptionHandler {

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, IdempotencyException ex) {
        handlerExceptionResolver.resolveException(request, response, null, ex);
    }
}