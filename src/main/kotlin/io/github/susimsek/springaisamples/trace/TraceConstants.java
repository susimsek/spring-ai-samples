package io.github.susimsek.springaisamples.trace;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TraceConstants {

    public static final String REQUEST_ID_HEADER_NAME = "X-Request-ID";
    public static final String CORRELATION_ID_HEADER_NAME = "X-Correlation-ID";
    public static final String TRACE_ID = "traceId";
    public static final String SPAN_ID = "spanId";
    public static final String REQUEST_ID = "requestId";
    public static final String CORRELATION_ID = "correlationId";
    public static final String REQUEST_ID_PATTERN_REGEX = "^[a-zA-Z0-9-]*$";
    public static final String CORRELATION_ID_PATTERN_REGEX = "^[a-zA-Z0-9-]*$";
}