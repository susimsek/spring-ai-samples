package io.github.susimsek.springaisamples.trace;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Constants for Spring Security authorities.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TraceConstants {

    public static final String REQUEST_ID_HEADER_NAME = "X-Request-ID";
    public static final String CORRELATION_ID_HEADER_NAME = "X-Correlation-ID";
}
