package io.github.susimsek.springaisamples.logging.strategy;

import java.net.URI;
import org.springframework.http.HttpHeaders;

public interface ObfuscationStrategy {
    HttpHeaders maskHeaders(HttpHeaders headers);

    String maskBody(String body);

    URI maskUriParameters(URI uri);

    Object[] maskArguments(Object[] arguments);

    Object maskResult(Object result);
}