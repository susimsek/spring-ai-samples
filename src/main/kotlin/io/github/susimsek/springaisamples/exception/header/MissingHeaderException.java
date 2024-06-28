package io.github.susimsek.springaisamples.exception.header;

import lombok.Getter;

@Getter
public class MissingHeaderException extends RuntimeException {

    private final String headerName;

    public MissingHeaderException(String headerName) {
        super("Missing required header: " + headerName);
        this.headerName = headerName;
    }
}