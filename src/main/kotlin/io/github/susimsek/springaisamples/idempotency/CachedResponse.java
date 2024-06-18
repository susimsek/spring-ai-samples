package io.github.susimsek.springaisamples.idempotency;

import java.util.Map;

public record CachedResponse(
    int status,
    Map<String, String> headers,
    String body) {
}