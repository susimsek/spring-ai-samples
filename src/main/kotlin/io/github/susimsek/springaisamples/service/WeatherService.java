package io.github.susimsek.springaisamples.service;

import io.github.susimsek.springaisamples.client.WeatherClient;
import io.github.susimsek.springaisamples.model.WeatherRequest;
import io.github.susimsek.springaisamples.model.WeatherResponse;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService implements Function<WeatherRequest, WeatherResponse> {

    private final WeatherClient weatherClient;

    @Override
    public WeatherResponse apply(WeatherRequest weatherRequest) {
        log.info("Received WeatherRequest for city: {}", weatherRequest.city());
        WeatherResponse response = weatherClient.getCurrentWeather(weatherRequest.city());
        log.info("Successfully fetched weather data for city: {}", weatherRequest.city());
        return response;
    }
}