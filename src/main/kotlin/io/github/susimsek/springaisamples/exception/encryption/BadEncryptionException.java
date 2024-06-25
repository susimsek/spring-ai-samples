package io.github.susimsek.springaisamples.exception.encryption;

public class BadEncryptionException extends EncryptionException {

    public BadEncryptionException(String message) {
        super(message);
    }

    public BadEncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}