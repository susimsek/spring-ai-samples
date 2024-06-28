package io.github.susimsek.springaisamples.exception.trace;

public class MissingRequestIdException extends TraceException {

    public MissingRequestIdException(String message) {
        super(message);
    }

    public MissingRequestIdException(String message, Throwable cause) {
        super(message, cause);
    }
}