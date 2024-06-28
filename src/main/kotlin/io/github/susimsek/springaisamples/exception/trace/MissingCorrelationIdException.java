package io.github.susimsek.springaisamples.exception.trace;

public class MissingCorrelationIdException extends TraceException {

    public MissingCorrelationIdException(String message) {
        super(message);
    }

    public MissingCorrelationIdException(String message, Throwable cause) {
        super(message, cause);
    }
}