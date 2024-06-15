package io.github.susimsek.springaisamples.exception.security;

public class MissingJwsException extends JwsException {

    public MissingJwsException(String message) {
        super(message);
    }

    public MissingJwsException(String message, Throwable cause) {
        super(message, cause);
    }
}