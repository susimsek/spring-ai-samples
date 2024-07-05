package io.github.susimsek.springaisamples.client;

import io.github.susimsek.springaisamples.dto.WeatherResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface WeatherClient {

    @GetExchange("/current.json?key={apiKey}&aqi=yes")
    WeatherResponse getCurrentWeather(
        @RequestParam("q") String location);
}