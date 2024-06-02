package io.github.susimsek.springaisamples.logging.interceptor;

import io.github.susimsek.springaisamples.logging.config.LoggingProperties;
import io.github.susimsek.springaisamples.logging.enums.HttpLogType;
import io.github.susimsek.springaisamples.logging.enums.LogLevel;
import io.github.susimsek.springaisamples.logging.formatter.LogFormatter;
import io.github.susimsek.springaisamples.logging.model.HttpLog;
import io.github.susimsek.springaisamples.logging.utils.BufferingClientHttpResponseWrapper;
import io.github.susimsek.springaisamples.logging.utils.Obfuscator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestClientLoggingInterceptor implements ClientHttpRequestInterceptor {

    private final LoggingProperties loggingProperties;
    private final LogFormatter logFormatter;
    private final Obfuscator obfuscator;

    @Override
    public @NonNull ClientHttpResponse intercept(
        @NonNull HttpRequest request, @NonNull byte[] body, @NonNull ClientHttpRequestExecution execution)
        throws IOException {

        LogLevel logLevel = loggingProperties.getLevel();
        if (logLevel == LogLevel.NONE) {
            return execution.execute(request, body);
        }

        logRequest(request, body, logLevel);

        try {
            ClientHttpResponse response = execution.execute(request, body);
            ClientHttpResponse responseWrapper = new BufferingClientHttpResponseWrapper(response);
            String responseBody = readResponseBody(responseWrapper);
            logResponse(request, responseWrapper, responseBody, logLevel);

            return responseWrapper;
        } catch (IOException e) {
            handleException(request, e);
            throw e;
        }
    }

    private void logRequest(HttpRequest request, byte[] body, LogLevel logLevel) {
        HttpLog.HttpLogBuilder logBuilder = HttpLog.builder()
            .type(HttpLogType.REQUEST)
            .method(request.getMethod().name())
            .uri(obfuscator.maskUriParameters(request.getURI()));

        addHeadersAndBody(logBuilder, request.getHeaders(), new String(body, StandardCharsets.UTF_8), logLevel);
        log.info("HTTP Request: {}", logFormatter.format(logBuilder.build()));
    }

    private void logResponse(HttpRequest request, ClientHttpResponse response, String responseBody, LogLevel logLevel)
        throws IOException {
        HttpLog.HttpLogBuilder logBuilder = HttpLog.builder()
            .type(HttpLogType.RESPONSE)
            .method(request.getMethod().name())
            .statusCode(response.getStatusCode().value());

        addHeadersAndBody(logBuilder, response.getHeaders(), responseBody, logLevel);
        log.info("HTTP Response: {}", logFormatter.format(logBuilder.build()));
    }

    private void addHeadersAndBody(HttpLog.HttpLogBuilder logBuilder,
                                   HttpHeaders headers,
                                   String body,
                                   LogLevel logLevel) {
        if (shouldLog(logLevel, LogLevel.HEADERS)) {
            logBuilder.headers(obfuscator.maskHeaders(headers));
        }
        if (shouldLog(logLevel, LogLevel.FULL)) {
            logBuilder.body(obfuscator.maskBody(body));
        }
    }

    private String readResponseBody(ClientHttpResponse response) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
            return bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    private void handleException(HttpRequest request, Exception e) {
        log.error("Exception occurred while executing request: {}", request.getURI(), e);
    }

    private boolean shouldLog(LogLevel currentLevel, LogLevel requiredLevel) {
        return currentLevel.ordinal() >= requiredLevel.ordinal();
    }
}