package io.github.susimsek.springaisamples.exception.security;

import io.github.susimsek.springaisamples.exception.encryption.EncryptionExceptionHandler;
import io.github.susimsek.springaisamples.exception.encryption.JweException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
@RequiredArgsConstructor
public class SecurityProblemSupport implements
    AuthenticationEntryPoint, AccessDeniedHandler,
    SignatureExceptionHandler, EncryptionExceptionHandler {

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException ex) {
        handlerExceptionResolver.resolveException(request, response, null, ex);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException ex) {
        handlerExceptionResolver.resolveException(request, response, null, ex);
    }


    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       JwsException ex) {
        handlerExceptionResolver.resolveException(request, response, null, ex);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       JweException ex)
        throws IOException, ServletException {
        handlerExceptionResolver.resolveException(request, response, null, ex);
    }
}