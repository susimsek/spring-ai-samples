package io.github.susimsek.springaisamples.exception.security;

public class JwsEncodingException extends JwsException {

    public JwsEncodingException(String message) {
        super(message);
    }

    public JwsEncodingException(String message, Throwable cause) {
        super(message, cause);
    }
}