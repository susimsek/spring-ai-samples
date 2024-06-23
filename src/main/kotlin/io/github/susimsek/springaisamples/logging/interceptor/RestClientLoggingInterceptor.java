package io.github.susimsek.springaisamples.logging.interceptor;

import io.github.susimsek.springaisamples.logging.enums.Source;
import io.github.susimsek.springaisamples.logging.handler.LoggingHandler;
import io.github.susimsek.springaisamples.logging.utils.BufferingClientHttpResponseWrapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.util.StopWatch;
import org.springframework.util.StreamUtils;

@Slf4j
@RequiredArgsConstructor
public class RestClientLoggingInterceptor implements ClientHttpRequestInterceptor {

    private final LoggingHandler loggingHandler;

    @Override
    @NonNull
    public ClientHttpResponse intercept(
        @NonNull HttpRequest request, @NonNull byte[] body, @NonNull ClientHttpRequestExecution execution)
        throws IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        if (shouldNotLog(request)) {
            return execution.execute(request, body);
        }

        logRequest(request, body);

        ClientHttpResponse response;
        try {
            response = execution.execute(request, body);
            response = new BufferingClientHttpResponseWrapper(response);
            stopWatch.stop();
            long duration = stopWatch.getTotalTimeMillis();
            logResponse(request, response, duration);
        } catch (IOException e) {
            log.error("IOException occurred during request execution or response logging", e);
            stopWatch.stop();
            long duration = stopWatch.getTotalTimeMillis();
            logErrorResponse(request, duration);
            throw e;
        }

        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        loggingHandler.logRequest(
            request.getMethod(), request.getURI(), request.getHeaders(), body, Source.CLIENT
        );
    }

    private void logResponse(HttpRequest request, ClientHttpResponse response, long duration) throws IOException {
        byte[] responseBody = StreamUtils.copyToByteArray(response.getBody());
        loggingHandler.logResponse(
            request.getMethod(),
            request.getURI(),
            response.getStatusCode().value(),
            response.getHeaders(),
            responseBody,
            Source.CLIENT,
            duration
        );
    }

    private void logErrorResponse(HttpRequest request, long duration) {
        loggingHandler.logResponse(
            request.getMethod(), request.getURI(),
            0, request.getHeaders(), null, Source.CLIENT,
            duration
        );
    }

    private boolean shouldNotLog(HttpRequest request) {
        return loggingHandler.shouldNotLog(request);
    }
}