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
    SIGNATURE(HIGHEST_PRECEDENCE),
    XSS(HIGHEST_PRECEDENCE + 1),
    IDEMPOTENCY(HIGHEST_PRECEDENCE + 2),
    TRACE(HIGHEST_PRECEDENCE + 3),
    RATE_LIMIT(HIGHEST_PRECEDENCE + 4),
    LOGGING(LOWEST_PRECEDENCE);

    private final int order;
}