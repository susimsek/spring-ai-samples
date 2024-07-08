package io.github.susimsek.springaisamples.enums;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;


@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum FilterOrder {
    LOGGING(HIGHEST_PRECEDENCE + 1),
    API_VERSION(HIGHEST_PRECEDENCE + 2),
    HEADER_VALIDATION(HIGHEST_PRECEDENCE + 3),
    TRACE(HIGHEST_PRECEDENCE + 4),
    DECRYPTION(HIGHEST_PRECEDENCE + 5),
    SIGNATURE_VERIFICATION(HIGHEST_PRECEDENCE + 6),
    JWT(HIGHEST_PRECEDENCE + 7),
    XSS(HIGHEST_PRECEDENCE + 8),
    IDEMPOTENCY(HIGHEST_PRECEDENCE + 9),
    RATE_LIMIT(HIGHEST_PRECEDENCE + 10),
    SIGNATURE(HIGHEST_PRECEDENCE + 11),
    ENCRYPTION(HIGHEST_PRECEDENCE + 12),
    CIRCUIT_BREAKER(HIGHEST_PRECEDENCE + 13);

    private final int order;
}