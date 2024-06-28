package io.github.susimsek.springaisamples.exception.trace;

public class TraceException extends RuntimeException {

    public TraceException(String message) {
        super(message);
    }

    public TraceException(String message, Throwable cause) {
        super(message, cause);
    }
}