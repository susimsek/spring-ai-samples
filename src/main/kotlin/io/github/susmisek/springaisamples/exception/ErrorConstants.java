package io.github.susmisek.springaisamples.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorConstants {
    public static final String PROBLEM_VIOLATION_KEY = "violations";
    public static final String MEDIA_TYPE_NOT_ACCEPTABLE = "error.mediaType.notAcceptable";
    public static final String MEDIA_TYPE_NOT_SUPPORTED = "error.mediaType.notSupported";
    public static final String REQUEST_METHOD_NOT_SUPPORTED = "error.requestMethod.notSupported";
    public static final String VALIDATION_ERROR = "error.validation";
    public static final String NO_HANDLER_FOUND = "error.noHandlerFound";
    public static final String INTERNAL_SERVER_ERROR = "error.internalServerError";
}