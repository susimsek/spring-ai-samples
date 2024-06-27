package io.github.susimsek.springaisamples.exception.header;

import lombok.Getter;

@Getter
public class HeaderException extends RuntimeException {

    private final String headerName;

    public HeaderException(String headerName, String message) {
        super(message);
        this.headerName = headerName;
    }
}