package io.github.susimsek.springaisamples.enums;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum FilterOrder {
    TRACE(HIGHEST_PRECEDENCE),
    LOGGING(HIGHEST_PRECEDENCE + 1),
    HEADER_VALIDATION(HIGHEST_PRECEDENCE + 2),
    DECRYPTION(HIGHEST_PRECEDENCE + 3),
    SIGNATURE_VERIFICATION(HIGHEST_PRECEDENCE + 4),
    JWT(HIGHEST_PRECEDENCE + 5),
    XSS(HIGHEST_PRECEDENCE + 6),
    IDEMPOTENCY(HIGHEST_PRECEDENCE + 7),
    RATE_LIMIT(HIGHEST_PRECEDENCE + 8),
    SIGNATURE(HIGHEST_PRECEDENCE + 9),
    ENCRYPTION(HIGHEST_PRECEDENCE + 10),
    CIRCUIT_BREAKER(HIGHEST_PRECEDENCE + 11);

    private final int order;
}