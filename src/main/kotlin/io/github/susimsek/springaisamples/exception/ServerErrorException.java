package io.github.susimsek.springaisamples.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class ServerErrorException extends RuntimeException {
    private final int code;
    private final HttpStatusCode status;

    public ServerErrorException(int code, String message, HttpStatusCode status) {
        super(message);
        this.code = code;
        this.status = status;
    }

}
