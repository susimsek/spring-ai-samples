package io.github.susimsek.springaisamples.exception.security;

public class JwsException extends RuntimeException {

    public JwsException(String message) {
        super(message);
    }

    public JwsException(String message, Throwable cause) {
        super(message, cause);
    }
}