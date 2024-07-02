package io.github.susimsek.springaisamples.exception.versioning;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
@RequiredArgsConstructor
public class ApiVersionProblemSupport implements ApiVersionExceptionHandler {

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void handleUnsupportedApiVersionException(HttpServletRequest request,
                                                     HttpServletResponse response,
                                                     UnsupportedApiVersionException ex) {
        handlerExceptionResolver.resolveException(request, response, null, ex);
    }
}