package io.github.susimsek.springaisamples.exception.versioning;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ApiVersionExceptionHandler {

    void handleUnsupportedApiVersionException(
        HttpServletRequest request,
        HttpServletResponse response,
        UnsupportedApiVersionException exception) throws IOException, ServletException;
}