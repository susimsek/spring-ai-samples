package io.github.susimsek.springaisamples.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ClientErrorException extends RuntimeException {
    private final String code;
    private final HttpStatus status;
    private final String errorKey;

    public ClientErrorException(String code, String message, HttpStatus status,
                                String errorKey) {
        super(message);
        this.code = code;
        this.status = status;
        this.errorKey = errorKey;
    }

}
