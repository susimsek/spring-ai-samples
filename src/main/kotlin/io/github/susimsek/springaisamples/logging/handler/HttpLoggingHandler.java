package io.github.susimsek.springaisamples.logging.handler;

import io.github.susimsek.springaisamples.logging.config.LoggingProperties;
import io.github.susimsek.springaisamples.logging.enums.HttpLogType;
import io.github.susimsek.springaisamples.logging.enums.LogLevel;
import io.github.susimsek.springaisamples.logging.enums.Source;
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
    public void logRequest(String method, URI uri, HttpHeaders headers, byte[] body, Source source) {
        if (shouldNotLog(uri.getPath(), method)) {
            return;
        }

        HttpLog.HttpLogBuilder logBuilder = initLogBuilder(
            HttpLogType.REQUEST, method, uri, headers, source
        );
        if (isLogLevel(LogLevel.FULL)) {
            logBuilder.body(obfuscator.maskBody(new String(body, StandardCharsets.UTF_8)));
        }

        log("HTTP Request: {}", logFormatter.format(logBuilder.build()));
    }

    @Override
    public void logResponse(String method, URI uri, Integer statusCode, HttpHeaders headers,
                            byte[] responseBody, Source source) {
        if (shouldNotLog(uri.getPath(), method)) {
            return;
        }

        HttpLog.HttpLogBuilder logBuilder = initLogBuilder(
            HttpLogType.RESPONSE, method, uri, headers, source
        ).statusCode(statusCode);

        HttpStatus status = HttpStatus.valueOf(statusCode);

        if (isLogLevel(LogLevel.FULL) && status.is2xxSuccessful()) {
            logBuilder.body(obfuscator.maskBody(new String(responseBody, StandardCharsets.UTF_8)));
        } else if (shouldLogWithoutBody(status)) {
            logBuilder.body(null);
        } else if (status.is4xxClientError() || status.is5xxServerError()) {
            logBuilder.body(obfuscator.maskBody(new String(responseBody, StandardCharsets.UTF_8)));
        }

        log("HTTP Response: {}", logFormatter.format(logBuilder.build()));
    }

    private HttpLog.HttpLogBuilder initLogBuilder(HttpLogType type, String method, URI uri,
                                                  HttpHeaders headers, Source source) {
        return HttpLog.builder()
            .type(type)
            .method(method)
            .uri(uri)
            .headers(isLogLevel(LogLevel.HEADERS) ? obfuscator.maskHeaders(headers) : new HttpHeaders())
            .source(source);
    }

    private boolean isLogLevel(LogLevel level) {
        return loggingProperties.getHttp().getLevel().ordinal() >= level.ordinal();
    }

    private boolean shouldLogWithoutBody(HttpStatus status) {
        return status == HttpStatus.UNAUTHORIZED || status == HttpStatus.FORBIDDEN
            || status == HttpStatus.TOO_MANY_REQUESTS;
    }

    private void log(String message, String formattedLog) {
        log.info(message, formattedLog);
    }

    @Override
    public boolean shouldNotLog(String path, String method) {
        LogLevel logLevel = loggingProperties.getHttp().getLevel();
        return logLevel == LogLevel.NONE
            || !pathFilter.shouldInclude(path, method)
            || pathFilter.shouldExclude(path, method);
    }
}