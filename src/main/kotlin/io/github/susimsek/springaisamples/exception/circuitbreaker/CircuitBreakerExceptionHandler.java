package io.github.susimsek.springaisamples.exception.circuitbreaker;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface CircuitBreakerExceptionHandler {

    void handle(HttpServletRequest request, HttpServletResponse response,
                CircuitBreakerException exception) throws IOException, ServletException;
}