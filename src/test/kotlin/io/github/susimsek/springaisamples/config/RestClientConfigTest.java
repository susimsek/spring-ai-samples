package io.github.susimsek.springaisamples.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import io.github.susimsek.springaisamples.client.WeatherClient;
import io.github.susimsek.springaisamples.logging.interceptor.RestClientLoggingInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class RestClientConfigTest {

    @Mock
    private RestClientBuilderConfigurer restClientBuilderConfigurer;

    @Mock
    private ObjectProvider<RestClientLoggingInterceptor> restClientLoggingInterceptorProvider;

    @Mock
    private WeatherClientProperties weatherClientProperties;

    @Mock
    private RestClientLoggingInterceptor restClientLoggingInterceptor;

    @InjectMocks
    private RestClientConfig restClientConfig;

    @BeforeEach
    void setUp() {
        when(restClientBuilderConfigurer.configure(any(RestClient.Builder.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void testRestClientBuilderWithoutLoggingInterceptor() {
        when(restClientLoggingInterceptorProvider.getIfAvailable()).thenReturn(null);

        RestClient.Builder builder = restClientConfig.restClientBuilder(
            restClientBuilderConfigurer, restClientLoggingInterceptorProvider);

        assertNotNull(builder);
    }

    @Test
    void testRestClientBuilderWithLoggingInterceptor() {
        when(restClientLoggingInterceptorProvider.getIfAvailable()).thenReturn(restClientLoggingInterceptor);

        RestClient.Builder builder = restClientConfig.restClientBuilder(
            restClientBuilderConfigurer, restClientLoggingInterceptorProvider);

        assertNotNull(builder);
    }

    @Test
    void testWeatherClient() {
        when(weatherClientProperties.getApiUrl()).thenReturn("http://api.weather.com");
        when(weatherClientProperties.getApiKey()).thenReturn("dummyApiKey");

        RestClient.Builder builder = restClientConfig.restClientBuilder(
            restClientBuilderConfigurer, restClientLoggingInterceptorProvider);

        WeatherClient weatherClient = restClientConfig.weatherClient(builder, weatherClientProperties);

        assertNotNull(weatherClient);
    }
}