package io.github.susimsek.springaisamples.config;

import io.github.susimsek.springaisamples.client.WeatherClient;
import io.github.susimsek.springaisamples.dto.WeatherRequest;
import io.github.susimsek.springaisamples.dto.WeatherResponse;
import io.github.susimsek.springaisamples.service.WeatherService;
import java.util.function.Function;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

@Configuration
public class FunctionConfig {

    @Bean
    @Description("Get the current weather conditions for the given city.")
    public Function<WeatherRequest, WeatherResponse> currentWeatherFunction(
        WeatherClient weatherClient) {
        return new WeatherService(weatherClient);
    }

}