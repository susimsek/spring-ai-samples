package io.github.susimsek.springaisamples.exception.header;

import io.github.susimsek.springaisamples.exception.Violation;
import java.util.List;
import lombok.Getter;

@Getter
public class HeaderConstraintViolationException extends RuntimeException {
    private final transient List<Violation> violations;

    public HeaderConstraintViolationException(List<Violation> violations) {
        super("Header validation failed");
        this.violations = violations;
    }

}