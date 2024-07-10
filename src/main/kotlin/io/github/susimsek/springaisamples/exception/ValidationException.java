package io.github.susimsek.springaisamples.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends LocalizedException {

    public ValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}