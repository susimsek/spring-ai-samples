package io.github.susimsek.springaisamples.enums;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum FilterOrder {
    LOGGING(HIGHEST_PRECEDENCE),
    TRACE(HIGHEST_PRECEDENCE + 1),
    DECRYPTION(HIGHEST_PRECEDENCE + 2),
    SIGNATURE_VERIFICATION(HIGHEST_PRECEDENCE + 3),
    JWT(HIGHEST_PRECEDENCE + 4),
    XSS(HIGHEST_PRECEDENCE + 5),
    IDEMPOTENCY(HIGHEST_PRECEDENCE + 6),
    RATE_LIMIT(HIGHEST_PRECEDENCE + 7),
    SIGNATURE(HIGHEST_PRECEDENCE + 8),
    ENCRYPTION(HIGHEST_PRECEDENCE + 9),
    CIRCUIT_BREAKER(HIGHEST_PRECEDENCE + 10);

    private final int order;
}