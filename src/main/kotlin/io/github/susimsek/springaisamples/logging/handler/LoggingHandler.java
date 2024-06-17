package io.github.susimsek.springaisamples.logging.handler;

import io.github.susimsek.springaisamples.logging.enums.Source;
import java.net.URI;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public interface LoggingHandler {

    void logRequest(HttpMethod method, URI uri, HttpHeaders headers, byte[] body, Source source);

    void logResponse(HttpMethod method, URI uri, Integer statusCode, HttpHeaders headers, byte[] responseBody, Source source);

    boolean shouldNotLog(String path, HttpMethod method);
}