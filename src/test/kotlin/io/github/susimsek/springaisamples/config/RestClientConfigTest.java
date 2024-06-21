package io.github.susimsek.springaisamples.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.susimsek.springaisamples.client.WeatherClient;
import io.github.susimsek.springaisamples.logging.wrapper.HttpLoggingWrapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@ExtendWith(MockitoExtension.class)
class RestClientConfigTest {

    @Mock
    private RestClientBuilderConfigurer restClientBuilderConfigurer;

    @Mock
    private ObjectProvider<HttpLoggingWrapper> httpLoggingWrapperProvider;

    @Mock
    private WeatherClientProperties properties;

    @InjectMocks
    private RestClientConfig restClientConfig;

    @Test
    void testRestClientBuilder_WithLoggingWrapper() {
        HttpLoggingWrapper httpLoggingWrapper = mock(HttpLoggingWrapper.class);
        ClientHttpRequestInterceptor interceptor = mock(ClientHttpRequestInterceptor.class);

        when(httpLoggingWrapperProvider.getIfAvailable()).thenReturn(httpLoggingWrapper);
        when(httpLoggingWrapper.createRestClientInterceptor()).thenReturn(interceptor);
        when(restClientBuilderConfigurer.configure(any(RestClient.Builder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RestClient.Builder resultBuilder = restClientConfig.restClientBuilder(restClientBuilderConfigurer, httpLoggingWrapperProvider);

        assertNotNull(resultBuilder);
        verify(httpLoggingWrapperProvider).getIfAvailable();
        verify(httpLoggingWrapper).createRestClientInterceptor();
    }

    @Test
    void testRestClientBuilder_WithoutLoggingWrapper() {
        when(httpLoggingWrapperProvider.getIfAvailable()).thenReturn(null);
        when(restClientBuilderConfigurer.configure(any(RestClient.Builder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RestClient.Builder resultBuilder = restClientConfig.restClientBuilder(restClientBuilderConfigurer, httpLoggingWrapperProvider);

        assertNotNull(resultBuilder);
        verify(httpLoggingWrapperProvider).getIfAvailable();
    }

    @Test
    void testWeatherClient() {
        when(properties.getApiUrl()).thenReturn("http://example.com");
        when(properties.getApiKey()).thenReturn("testApiKey");

        RestClient.Builder restClientBuilder = RestClient.builder()
            .baseUrl("http://example.com")
            .defaultUriVariables(Map.of("apiKey", "testApiKey"));
        RestClient restClient = restClientBuilder.build();

        WeatherClient weatherClient = restClientConfig.weatherClient(restClientBuilder, null, properties);

        assertNotNull(weatherClient);

        // Verifying if the HttpServiceProxyFactory and WeatherClient are correctly created
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
        WeatherClient expectedClient = factory.createClient(WeatherClient.class);

        assertNotNull(expectedClient);
    }
}