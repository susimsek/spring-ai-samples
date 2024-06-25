package io.github.susimsek.springaisamples.exception.encryption;

public class JweException extends RuntimeException {

    public JweException(String message) {
        super(message);
    }

    public JweException(String message, Throwable cause) {
        super(message, cause);
    }
}