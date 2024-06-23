package io.github.susimsek.springaisamples.logging.strategy;

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

    @Override
    public Object[] maskArguments(Object[] arguments) {
        return arguments;
    }

    @Override
    public Object maskResult(Object result) {
        return result;
    }
}