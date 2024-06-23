package io.github.susimsek.springaisamples.logging.utils;

import io.github.susimsek.springaisamples.logging.strategy.ObfuscationStrategy;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;

@RequiredArgsConstructor
public class Obfuscator {

    private final ObfuscationStrategy obfuscationStrategy;

    public HttpHeaders maskHeaders(HttpHeaders headers) {
        return obfuscationStrategy.maskHeaders(headers);
    }

    public String maskBody(String body) {
        return obfuscationStrategy.maskBody(body);
    }

    public URI maskUriParameters(URI uri) {
        return obfuscationStrategy.maskUriParameters(uri);
    }

    public Object[] maskArguments(Object[] arguments) {
        return obfuscationStrategy.maskArguments(arguments);
    }

    public Object maskResult(Object result) {
        return obfuscationStrategy.maskResult(result);
    }
}