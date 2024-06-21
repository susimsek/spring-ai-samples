package io.github.susimsek.springaisamples.exception.restclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.susimsek.springaisamples.exception.ClientErrorException;
import io.github.susimsek.springaisamples.exception.ServerErrorException;
import java.io.IOException;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

@Component
@RequiredArgsConstructor
public class WeatherApiErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    @Override
    public boolean hasError(@NonNull ClientHttpResponse response) throws IOException {
        return (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError());
    }

    @Override
    public void handleError(@NonNull ClientHttpResponse response) throws IOException {
        WeatherApiError error = objectMapper.readValue(response.getBody(), WeatherApiError.class);
        HttpStatus status = HttpStatus.valueOf(response.getStatusCode().value());
        String errorKey = "weatherapi.error." + error.code();
        if (status.is4xxClientError()) {
            throw new ClientErrorException(String.valueOf(error.code()), error.message(), status, errorKey);
        } else if (status.is5xxServerError()) {
            throw new ServerErrorException(error.code(), error.message(), status);
        }
    }

    @Override
    public void handleError(@NonNull URI url,
                            @NonNull HttpMethod method,
                            @NonNull ClientHttpResponse response) throws IOException {
        handleError(response);
    }
}