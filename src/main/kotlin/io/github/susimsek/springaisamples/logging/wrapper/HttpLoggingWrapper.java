package io.github.susimsek.springaisamples.logging.wrapper;

import io.github.susimsek.springaisamples.logging.handler.HttpLoggingHandler;
import io.github.susimsek.springaisamples.logging.utils.BufferingClientHttpResponseWrapper;
import java.io.IOException;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

@RequiredArgsConstructor
public class HttpLoggingWrapper {

    private final HttpLoggingHandler httpLoggingHandler;

    public ClientHttpRequestInterceptor createRestClientInterceptor() {
        return this::interceptRestClientRequest;
    }

    private ClientHttpResponse interceptRestClientRequest(HttpRequest request,
                                                          byte[] body,
                                                          ClientHttpRequestExecution execution) throws IOException {
        logRequest(request.getMethod().name(), request.getURI(), request.getHeaders(), body);

        ClientHttpResponse response = execution.execute(request, body);
        ClientHttpResponse bufferedResponse = new BufferingClientHttpResponseWrapper(response);
        logResponse(request.getMethod().name(), request.getURI(),
            response.getStatusCode().value(), response.getHeaders(),
            bufferedResponse.getBody().readAllBytes());

        return bufferedResponse;
    }

    private void logRequest(String method, URI uri, HttpHeaders headers, byte[] body) {
        httpLoggingHandler.logRequest(method, uri, headers, body);
    }

    private void logResponse(String method, URI uri, int statusCode, HttpHeaders headers, byte[] body) {
        httpLoggingHandler.logResponse(method, uri, statusCode, headers, body);
    }
}