package io.github.susimsek.springaisamples.exception.encryption;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface EncryptionExceptionHandler {

    void handle(HttpServletRequest request, HttpServletResponse response,
                EncryptionException exception) throws IOException, ServletException;
}