package io.github.susimsek.springaisamples.enums;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum FilterOrder {
    DECRYPTION(HIGHEST_PRECEDENCE),
    SIGNATURE(HIGHEST_PRECEDENCE + 2),
    JWT(HIGHEST_PRECEDENCE + 3),
    XSS(HIGHEST_PRECEDENCE + 4),
    IDEMPOTENCY(HIGHEST_PRECEDENCE + 5),
    TRACE(HIGHEST_PRECEDENCE + 6),
    RATE_LIMIT(HIGHEST_PRECEDENCE + 7),
    ENCRYPTION(HIGHEST_PRECEDENCE + 8),
    CIRCUIT_BREAKER(HIGHEST_PRECEDENCE + 9),
    LOGGING(LOWEST_PRECEDENCE);

    private final int order;
}