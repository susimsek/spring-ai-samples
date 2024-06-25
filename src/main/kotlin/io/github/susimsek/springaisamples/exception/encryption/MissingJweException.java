package io.github.susimsek.springaisamples.exception.encryption;

public class MissingJweException extends JweException {

    public MissingJweException(String message) {
        super(message);
    }

    public MissingJweException(String message, Throwable cause) {
        super(message, cause);
    }
}