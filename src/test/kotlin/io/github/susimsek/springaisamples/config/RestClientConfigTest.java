package io.github.susimsek.springaisamples.config;

import io.github.susimsek.springaisamples.client.WeatherClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestClientConfigTest {

    @InjectMocks
    RestClientConfig restClientConfig;

    @Mock
    WeatherClientProperties weatherClientProperties;

    @Test
    void testWeatherClientBean() {
        when(weatherClientProperties.getApiUrl()).thenReturn("https://api.example.com");
        when(weatherClientProperties.getApiKey()).thenReturn("sampleapikey1234567890123456");

        WeatherClient weatherClient = restClientConfig.weatherClient(weatherClientProperties);
        assertNotNull(weatherClient);
    }
}