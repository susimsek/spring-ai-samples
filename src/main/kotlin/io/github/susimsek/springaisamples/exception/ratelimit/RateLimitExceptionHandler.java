package io.github.susimsek.springaisamples.exception.ratelimit;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface RateLimitExceptionHandler {

    void handle(HttpServletRequest request, HttpServletResponse response,
                RateLimitExceededException exception) throws IOException, ServletException;
}