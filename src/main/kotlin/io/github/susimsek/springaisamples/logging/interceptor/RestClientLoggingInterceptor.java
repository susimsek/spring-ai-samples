package io.github.susimsek.springaisamples.logging.interceptor;

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
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
public class RestClientLoggingInterceptor implements ClientHttpRequestInterceptor {

    private final HttpLoggingHandler httpLoggingHandler;

    @Override
    @NonNull
    public ClientHttpResponse intercept(
        @NonNull HttpRequest request, @NonNull byte[] body, @NonNull ClientHttpRequestExecution execution)
        throws IOException {
        logRequest(request, body);

        ClientHttpResponse response = executeRequest(request, body, execution);
        ClientHttpResponse bufferedResponse = new BufferingClientHttpResponseWrapper(response);

        logResponse(request, bufferedResponse);

        return bufferedResponse;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        String method = request.getMethod().name();
        URI uri = request.getURI();
        HttpHeaders headers = request.getHeaders();
        httpLoggingHandler.logRequest(method, uri, headers, body);
    }

    private ClientHttpResponse executeRequest(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {
        return execution.execute(request, body);
    }

    private void logResponse(HttpRequest request, ClientHttpResponse response) throws IOException {
        String method = request.getMethod().name();
        URI uri = request.getURI();
        int statusCode = response.getStatusCode().value();
        HttpHeaders headers = response.getHeaders();
        byte[] body = response.getBody().readAllBytes();
        httpLoggingHandler.logResponse(method, uri, statusCode, headers, body);
    }
}