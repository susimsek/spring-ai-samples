package io.github.susimsek.springaisamples.logging.handler;

import java.net.URI;
import org.springframework.http.HttpHeaders;


public interface LoggingHandler {

    void logRequest(String method, URI uri, HttpHeaders headers, byte[] body);

    void logResponse(String method, URI uri, int statusCode, HttpHeaders headers, byte[] responseBody);
}