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
    ENCRYPTION(HIGHEST_PRECEDENCE),
    SIGNATURE(HIGHEST_PRECEDENCE + 1),
    JWT(HIGHEST_PRECEDENCE + 2),
    XSS(HIGHEST_PRECEDENCE + 3),
    IDEMPOTENCY(HIGHEST_PRECEDENCE + 4),
    TRACE(HIGHEST_PRECEDENCE + 5),
    RATE_LIMIT(HIGHEST_PRECEDENCE + 6),
    CIRCUIT_BREAKER(HIGHEST_PRECEDENCE + 7),
    LOGGING(LOWEST_PRECEDENCE);

    private final int order;
}