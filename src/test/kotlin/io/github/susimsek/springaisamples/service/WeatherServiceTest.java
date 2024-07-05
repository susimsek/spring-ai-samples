package io.github.susimsek.springaisamples.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import io.github.susimsek.springaisamples.client.WeatherClient;
import io.github.susimsek.springaisamples.dto.Condition;
import io.github.susimsek.springaisamples.dto.Current;
import io.github.susimsek.springaisamples.dto.Location;
import io.github.susimsek.springaisamples.dto.WeatherRequest;
import io.github.susimsek.springaisamples.dto.WeatherResponse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private WeatherClient weatherClient;

    @InjectMocks
    private WeatherService weatherService;

    private WeatherRequest weatherRequest;
    private WeatherResponse weatherResponse;

    @BeforeEach
    void setUp() {
        weatherRequest = new WeatherRequest("Istanbul");

        Location location = new Location(
            "Istanbul", "Marmara", "Turkey", 41.01, 28.98, "Europe/Istanbul", 1625247600L, "2023-05-25 15:00");

        Condition condition = new Condition("Sunny", "Sunny icon", 1000);

        Current current = new Current(
            1625247600L, LocalDateTime.now(), 20.0, 68.0, 1, condition,
            10.0, 16.0, 180, "S", 1015.0, 30.0, 5.0, 0.2, 50, 75,
            20.0, 68.0, 20.0, 68.0, 25.0, 77.0, 15.0, 59.0, 10.0, 6.2, 5.0, 12.0, 19.2);

        weatherResponse = new WeatherResponse(location, current);
    }

    @Test
    void testApply() {
        when(weatherClient.getCurrentWeather(anyString())).thenReturn(weatherResponse);

        WeatherResponse response = weatherService.apply(weatherRequest);

        assertEquals(weatherResponse, response);
    }
}