package io.github.susimsek.springaisamples.logging.handler;

import io.github.susimsek.springaisamples.logging.enums.Source;
import java.net.URI;
import org.springframework.http.HttpHeaders;

public interface LoggingHandler {

    void logRequest(String method, URI uri, HttpHeaders headers, byte[] body, Source source);

    void logResponse(String method, URI uri, Integer statusCode, HttpHeaders headers, byte[] responseBody, Source source);

    boolean shouldNotLog(String path, String method);
}