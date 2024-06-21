package io.github.susimsek.springaisamples.exception;

import org.springframework.http.HttpStatusCode;

public class ClientErrorException extends LocalizedException {

    public ClientErrorException(String message, HttpStatusCode status) {
        super(message, status);
    }
}