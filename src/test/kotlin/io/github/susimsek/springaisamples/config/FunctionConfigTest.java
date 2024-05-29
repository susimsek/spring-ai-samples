package io.github.susimsek.springaisamples.config;

import io.github.susimsek.springaisamples.client.WeatherClient;
import io.github.susimsek.springaisamples.model.WeatherRequest;
import io.github.susimsek.springaisamples.model.WeatherResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class FunctionConfigTest {

    @InjectMocks
    FunctionConfig functionConfig;

    @Mock
    WeatherClient weatherClient;

    @Test
    void testCurrentWeatherFunctionBean() {
        Function<WeatherRequest, WeatherResponse> function = functionConfig.currentWeatherFunction(weatherClient);
        assertNotNull(function);
    }
}