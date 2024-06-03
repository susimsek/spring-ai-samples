package io.github.susimsek.springaisamples.logging.handler;

import io.github.susimsek.springaisamples.logging.config.LoggingProperties;
import io.github.susimsek.springaisamples.logging.enums.HttpLogType;
import io.github.susimsek.springaisamples.logging.enums.LogLevel;
import io.github.susimsek.springaisamples.logging.formatter.LogFormatter;
import io.github.susimsek.springaisamples.logging.model.HttpLog;
import io.github.susimsek.springaisamples.logging.utils.Obfuscator;
import io.github.susimsek.springaisamples.logging.utils.PathFilter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@Slf4j
@RequiredArgsConstructor
public class HttpLoggingHandler implements LoggingHandler {

    private final LoggingProperties loggingProperties;
    private final LogFormatter logFormatter;
    private final Obfuscator obfuscator;
    private final PathFilter pathFilter;

    @Override
    public void logRequest(String method, URI uri, HttpHeaders headers, byte[] body) {
        LogLevel logLevel = loggingProperties.getHttp().getLevel();
        if (logLevel == LogLevel.NONE || !shouldLog(uri.getPath(), method)) {
            return;
        }

        HttpLog.HttpLogBuilder logBuilder = HttpLog.builder()
            .type(HttpLogType.REQUEST)
            .method(method)
            .uri(uri)
            .headers(
                logLevel == LogLevel.HEADERS || logLevel == LogLevel.FULL
                    ? obfuscator.maskHeaders(headers) : new HttpHeaders()
            );

        if (logLevel == LogLevel.FULL) {
            logBuilder.body(obfuscator.maskBody(new String(body, StandardCharsets.UTF_8)));
        }

        String formattedLog = logFormatter.format(logBuilder.build());
        if (formattedLog != null) {
            log.info(formattedLog);
        }
    }

    @Override
    public void logResponse(
        String method, URI uri, int statusCode, HttpHeaders headers, byte[] responseBody) {
        LogLevel logLevel = loggingProperties.getHttp().getLevel();
        if (logLevel == LogLevel.NONE || !shouldLog(uri.getPath(), method)) {
            return;
        }

        HttpLog.HttpLogBuilder logBuilder = HttpLog.builder()
            .type(HttpLogType.RESPONSE)
            .method(method)
            .uri(uri)
            .statusCode(statusCode)
            .headers(
                logLevel == LogLevel.HEADERS || logLevel == LogLevel.FULL
                    ? obfuscator.maskHeaders(headers) : new HttpHeaders()
            );

        HttpStatus status = HttpStatus.valueOf(statusCode);
        if (logLevel == LogLevel.FULL && status.is2xxSuccessful()) {
            logBuilder.body(obfuscator.maskBody(new String(responseBody, StandardCharsets.UTF_8)));
        } else if (status == HttpStatus.UNAUTHORIZED || status == HttpStatus.FORBIDDEN
            || status == HttpStatus.TOO_MANY_REQUESTS) {
            logBuilder.body(null); // Log these responses without body
        } else if (status.is4xxClientError() || status.is5xxServerError()) {
            logBuilder.body(obfuscator.maskBody(
                new String(responseBody, StandardCharsets.UTF_8))); // Log other 4xx and 5xx errors with body
        }

        String formattedLog = logFormatter.format(logBuilder.build());
        if (formattedLog != null) {
            log.info(formattedLog);
        }
    }

    private boolean shouldLog(String path, String method) {
        return pathFilter.shouldInclude(path, method) && !pathFilter.shouldExclude(path, method);
    }
}