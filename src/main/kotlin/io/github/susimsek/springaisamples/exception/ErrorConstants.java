package io.github.susimsek.springaisamples.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorConstants {
    public static final String PROBLEM_VIOLATION_KEY = "violations";
    public static final String MEDIA_TYPE_NOT_ACCEPTABLE = "error.mediaType.notAcceptable";
    public static final String MEDIA_TYPE_NOT_SUPPORTED = "error.mediaType.notSupported";
    public static final String REQUEST_METHOD_NOT_SUPPORTED = "error.requestMethod.notSupported";
    public static final String MESSAGE_NOT_READABLE = "error.messageNotReadable";
    public static final String TYPE_MISMATCH = "error.typeMismatch";
    public static final String MISSING_PARAMETER = "error.missingParameter";
    public static final String MISSING_PART = "error.missingPart";
    public static final String REQUEST_BINDING_ERROR = "error.requestBindingError";
    public static final String MULTIPART_ERROR = "error.multipartError";
    public static final String VALIDATION_ERROR = "error.validation";
    public static final String ENCRYPTION = "error.encryption";
    public static final String AUTHENTICATION = "error.authentication";
    public static final String SIGNATURE = "error.signature";
    public static final String ACCESS_DENIED = "error.accessDenied";
    public static final String NO_HANDLER_FOUND = "error.noHandlerFound";
    public static final String RATE_LIMITING_ERROR = "error.rateLimiting";
    public static final String INTERNAL_SERVER_ERROR = "error.internalServerError";
    public static final String IDEMPOTENCY_KEY_MISSING = "error.idempotencyKey.missing";
    public static final String UNSUPPORTED_OPERATION = "error.unsupportedOperation";
    public static final String CIRCUIT_BREAKER_ERROR = "error.circuitBreaker";
    public static final String GATEWAY_TIMEOUT = "error.gatewayTimeout";
}