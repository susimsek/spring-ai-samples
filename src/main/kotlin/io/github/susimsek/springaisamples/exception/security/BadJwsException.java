package io.github.susimsek.springaisamples.exception.security;

public class BadJwsException extends JwsException {

    public BadJwsException(String message) {
        super(message);
    }

    public BadJwsException(String message, Throwable cause) {
        super(message, cause);
    }
}