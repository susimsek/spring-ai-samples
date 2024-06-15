package io.github.susimsek.springaisamples.logging.filter;

import io.github.susimsek.springaisamples.logging.enums.Source;
import io.github.susimsek.springaisamples.logging.handler.LoggingHandler;
import io.github.susimsek.springaisamples.logging.utils.CachedBodyHttpServletRequestWrapper;
import io.github.susimsek.springaisamples.logging.utils.CachedBodyHttpServletResponseWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
@Order()
public class LoggingFilter extends OncePerRequestFilter {

    private final LoggingHandler loggingHandler;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (!loggingHandler.shouldNotLog(request.getRequestURI(), request.getMethod())) {
            CachedBodyHttpServletRequestWrapper wrappedRequest = new CachedBodyHttpServletRequestWrapper(request);
            CachedBodyHttpServletResponseWrapper wrappedResponse = new CachedBodyHttpServletResponseWrapper(response);

            filterChain.doFilter(wrappedRequest, wrappedResponse);
            logRequestAndResponse(wrappedRequest, wrappedResponse);
            wrappedResponse.copyBodyToResponse();
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private void logRequestAndResponse(CachedBodyHttpServletRequestWrapper request,
                                       CachedBodyHttpServletResponseWrapper response) {
        try {
            URI uri = new URI(request.getRequestURL().toString());
            HttpHeaders requestHeaders = getHeaders(request);
            HttpHeaders responseHeaders = getHeaders(response);

            loggingHandler.logRequest(
                request.getMethod(),
                uri,
                requestHeaders,
                request.getBody(),
                Source.SERVER
            );
            loggingHandler.logResponse(
                request.getMethod(),
                uri,
                response.getStatus(),
                responseHeaders,
                response.getBody(),
                Source.SERVER
            );
        } catch (URISyntaxException e) {
            log.error("Invalid URI Syntax for request: {}", request.getRequestURL(), e);
        }
    }

    private HttpHeaders getHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        request.getHeaderNames().asIterator().forEachRemaining(
            headerName -> headers.add(headerName, request.getHeader(headerName))
        );
        return headers;
    }

    private HttpHeaders getHeaders(HttpServletResponse response) {
        HttpHeaders headers = new HttpHeaders();
        response.getHeaderNames().forEach(
            headerName -> headers.add(headerName, response.getHeader(headerName))
        );
        return headers;
    }
}