package io.github.susimsek.springaisamples.exception.encryption;

public class BadJweException extends JweException {

    public BadJweException(String message) {
        super(message);
    }

    public BadJweException(String message, Throwable cause) {
        super(message, cause);
    }
}