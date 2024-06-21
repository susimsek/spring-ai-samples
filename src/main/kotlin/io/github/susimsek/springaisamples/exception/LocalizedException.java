package io.github.susimsek.springaisamples.exception;

import java.util.Map;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class LocalizedException extends RuntimeException {
    private final transient Object[] args;
    private final Map<String, String> namedArgs;
    private final HttpStatusCode status;

    public LocalizedException(String message, HttpStatusCode status) {
        super(message);
        this.status = status;
        this.args = null;
        this.namedArgs = null;
    }

    public LocalizedException(String message, HttpStatusCode status, Object... args) {
        super(message);
        this.status = status;
        this.args = args;
        this.namedArgs = null;
    }
 
    public LocalizedException(String message, HttpStatusCode status, Map<String, String> namedArgs) {
        super(message);
        this.status = status;
        this.args = null;
        this.namedArgs = namedArgs;
    }
}