package io.github.susimsek.springaisamples.exception.idempotency;

public class MissingIdempotencyKeyException extends IdempotencyException {

    public MissingIdempotencyKeyException(String message) {
        super(message);
    }

    public MissingIdempotencyKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}