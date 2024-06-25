package io.github.susimsek.springaisamples.exception.encryption;

public class JweEncodingException extends JweException {

    public JweEncodingException(String message) {
        super(message);
    }

    public JweEncodingException(String message, Throwable cause) {
        super(message, cause);
    }
}