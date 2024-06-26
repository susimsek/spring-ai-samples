package io.github.susimsek.springaisamples.logging.filter;

import io.github.susimsek.springaisamples.logging.enums.Source;
import io.github.susimsek.springaisamples.logging.handler.LoggingHandler;
import io.github.susimsek.springaisamples.utils.CachedBodyHttpServletRequestWrapper;
import io.github.susimsek.springaisamples.utils.HttpHeadersUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@RequiredArgsConstructor
public class LoggingFilter extends OncePerRequestFilter  implements Ordered {

    private final LoggingHandler loggingHandler;

    @Override
    public int getOrder() {
        return loggingHandler.getOrder();
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return loggingHandler.shouldNotLog(request);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        CachedBodyHttpServletRequestWrapper wrappedRequest = new CachedBodyHttpServletRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        filterChain.doFilter(wrappedRequest, wrappedResponse);

        stopWatch.stop();
        long duration = stopWatch.getTotalTimeMillis();

        logRequestAndResponse(wrappedRequest, wrappedResponse, duration);
        wrappedResponse.copyBodyToResponse();
    }

    private void logRequestAndResponse(CachedBodyHttpServletRequestWrapper request,
                                       ContentCachingResponseWrapper response,
                                       long duration) {
        try {
            URI uri = new URI(request.getRequestURL().toString());
            HttpHeaders requestHeaders = HttpHeadersUtil.convertToHttpHeaders(request);
            HttpHeaders responseHeaders = HttpHeadersUtil.convertToHttpHeaders(response);

            loggingHandler.logRequest(
                HttpMethod.valueOf(request.getMethod()),
                uri,
                requestHeaders,
                request.getContentAsByteArray(),
                Source.SERVER
            );
            loggingHandler.logResponse(
                HttpMethod.valueOf(request.getMethod()),
                uri,
                response.getStatus(),
                responseHeaders,
                response.getContentAsByteArray(),
                Source.SERVER,
                duration
            );
        } catch (URISyntaxException e) {
            log.error("Invalid URI Syntax for request: {}", request.getRequestURL(), e);
        }
    }
}