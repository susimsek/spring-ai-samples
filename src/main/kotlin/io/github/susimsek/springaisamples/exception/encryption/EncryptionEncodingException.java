package io.github.susimsek.springaisamples.exception.encryption;

public class EncryptionEncodingException extends RuntimeException {

    public EncryptionEncodingException(String message) {
        super(message);
    }

    public EncryptionEncodingException(String message, Throwable cause) {
        super(message, cause);
    }
}