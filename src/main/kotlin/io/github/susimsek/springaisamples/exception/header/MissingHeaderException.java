package io.github.susimsek.springaisamples.exception.header;

import lombok.Getter;

@Getter
public class MissingHeaderException extends HeaderException {

    public MissingHeaderException(String headerName) {
        super(headerName, "Missing required header: " + headerName);
    }
}