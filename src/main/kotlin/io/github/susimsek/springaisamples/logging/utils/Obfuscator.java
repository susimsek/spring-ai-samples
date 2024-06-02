package io.github.susimsek.springaisamples.logging.utils;

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
}