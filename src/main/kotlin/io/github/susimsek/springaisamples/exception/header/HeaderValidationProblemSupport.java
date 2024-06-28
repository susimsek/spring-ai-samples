package io.github.susimsek.springaisamples.exception.header;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
@RequiredArgsConstructor
public class HeaderValidationProblemSupport implements HeaderValidationExceptionHandler {

    private final HandlerExceptionResolver handlerExceptionResolver;


    @Override
    public void handleMissingHeaderException(HttpServletRequest request, HttpServletResponse response,
                       MissingHeaderException ex) {
        handlerExceptionResolver.resolveException(request, response, null, ex);
    }

    @Override
    public void handleHeaderValidationException(HttpServletRequest request, HttpServletResponse response,
                                                   HeaderConstraintViolationException ex) {
        handlerExceptionResolver.resolveException(request, response, null, ex);
    }
}