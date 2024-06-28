package io.github.susimsek.springaisamples.exception.trace;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
@RequiredArgsConstructor
public class TraceProblemSupport implements TraceExceptionHandler {

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, TraceException ex)
        throws IOException, ServletException {
        handlerExceptionResolver.resolveException(request, response, null, ex);
    }
}