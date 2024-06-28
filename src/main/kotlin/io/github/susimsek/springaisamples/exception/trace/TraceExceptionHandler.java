package io.github.susimsek.springaisamples.exception.trace;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface TraceExceptionHandler {

    void handle(HttpServletRequest request, HttpServletResponse response,
                                      TraceException exception) throws IOException, ServletException;
}