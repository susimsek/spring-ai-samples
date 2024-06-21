package io.github.susimsek.springaisamples.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ServerErrorException extends RuntimeException {
    private final int code;
    private final HttpStatus status;

    public ServerErrorException(int code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

}