package io.github.susimsek.springaisamples.exception.idempotency;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IdempotencyExceptionHandler {

    void handle(HttpServletRequest request, HttpServletResponse response,
                  IdempotencyException exception) throws IOException,
        ServletException;
}