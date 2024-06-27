package io.github.susimsek.springaisamples.exception.header;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface HeaderValidationExceptionHandler {

    void handle(HttpServletRequest request, HttpServletResponse response,
                                      HeaderException exception) throws IOException, ServletException;
}