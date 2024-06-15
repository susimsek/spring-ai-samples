package io.github.susimsek.springaisamples.exception.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface SignatureExceptionHandler {

    void handle(HttpServletRequest request, HttpServletResponse response, JwsException exception) throws IOException,
        ServletException;
}