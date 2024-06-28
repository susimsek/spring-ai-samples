package io.github.susimsek.springaisamples.exception.header;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
@RequiredArgsConstructor
public class HeaderValidationProblemSupport implements HeaderValidationExceptionHandler {

    private final HandlerExceptionResolver handlerExceptionResolver;


    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, HeaderException ex)
        throws IOException, ServletException {
        handlerExceptionResolver.resolveException(request, response, null, ex);
    }

    @Override
    public void handleHeaderValidationException(HttpServletRequest request, HttpServletResponse response,
                                                   HeaderConstraintViolationException ex) {
        handlerExceptionResolver.resolveException(request, response, null, ex);
    }
}