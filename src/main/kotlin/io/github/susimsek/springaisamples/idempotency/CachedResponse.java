package io.github.susimsek.springaisamples.idempotency;

import org.springframework.http.HttpHeaders;

public record CachedResponse(
    int status,
    HttpHeaders headers,
    String body) {
}