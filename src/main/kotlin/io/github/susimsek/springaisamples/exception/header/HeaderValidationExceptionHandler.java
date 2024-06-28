package io.github.susimsek.springaisamples.exception.header;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface HeaderValidationExceptionHandler {

    void handleMissingHeaderException(
        HttpServletRequest request,
        HttpServletResponse response,
        MissingHeaderException exception) throws IOException, ServletException;

    void handleHeaderValidationException(
        HttpServletRequest request,
        HttpServletResponse response,
        HeaderConstraintViolationException exception) throws IOException, ServletException;
}