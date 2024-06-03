package io.github.susimsek.springaisamples.logging.wrapper;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.susimsek.springaisamples.logging.handler.HttpLoggingHandler;
import io.github.susimsek.springaisamples.logging.interceptor.RestClientLoggingInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.client.ClientHttpRequestInterceptor;

@ExtendWith(MockitoExtension.class)
class HttpLoggingWrapperTest {

    @Mock
    private HttpLoggingHandler httpLoggingHandler;

    @InjectMocks
    private HttpLoggingWrapper httpLoggingWrapper;

    @BeforeEach
    void setUp() {
        httpLoggingWrapper = new HttpLoggingWrapper(httpLoggingHandler);
    }

    @Test
    void createRestClientInterceptor_shouldReturnRestClientLoggingInterceptor() {
        // When
        ClientHttpRequestInterceptor interceptor = httpLoggingWrapper.createRestClientInterceptor();

        // Then
        assertNotNull(interceptor);
        assertInstanceOf(RestClientLoggingInterceptor.class, interceptor);
    }
}