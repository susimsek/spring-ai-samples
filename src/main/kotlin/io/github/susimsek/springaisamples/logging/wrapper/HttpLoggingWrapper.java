package io.github.susimsek.springaisamples.logging.wrapper;

import io.github.susimsek.springaisamples.logging.handler.LoggingHandler;
import io.github.susimsek.springaisamples.logging.interceptor.RestClientLoggingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.client.ClientHttpRequestInterceptor;

@RequiredArgsConstructor
public class HttpLoggingWrapper {

    private final LoggingHandler loggingHandler;

    public ClientHttpRequestInterceptor createRestClientInterceptor() {
        return new RestClientLoggingInterceptor(loggingHandler);
    }
}