package io.github.susimsek.springaisamples.logging.utils;

import java.net.URI;
import org.springframework.http.HttpHeaders;

public class NoOpObfuscationStrategy implements ObfuscationStrategy {

    @Override
    public HttpHeaders maskHeaders(HttpHeaders headers) {
        return headers;
    }

    @Override
    public String maskBody(String body) {
        return body;
    }

    @Override
    public URI maskUriParameters(URI uri) {
        return uri;
    }
}