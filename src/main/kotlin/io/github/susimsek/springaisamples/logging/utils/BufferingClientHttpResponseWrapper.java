package io.github.susimsek.springaisamples.logging.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;

public class BufferingClientHttpResponseWrapper implements ClientHttpResponse {

    private final ClientHttpResponse response;
    private final byte[] body;

    public BufferingClientHttpResponseWrapper(ClientHttpResponse response) throws IOException {
        this.response = response;
        this.body = response.getBody().readAllBytes();
    }

    @Override
    public @NonNull HttpStatus getStatusCode() throws IOException {
        return HttpStatus.valueOf(this.response.getStatusCode().value());
    }

    @Override
    public @NonNull String getStatusText() throws IOException {
        return this.response.getStatusText();
    }

    @Override
    public void close() {
        this.response.close();
    }

    @Override
    public @NonNull InputStream getBody() {
        return new ByteArrayInputStream(this.body);
    }

    @Override
    public @NonNull HttpHeaders getHeaders() {
        return this.response.getHeaders();
    }
}