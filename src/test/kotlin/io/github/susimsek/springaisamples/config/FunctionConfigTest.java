package io.github.susimsek.springaisamples.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.susimsek.springaisamples.client.WeatherClient;
import io.github.susimsek.springaisamples.dto.WeatherRequest;
import io.github.susimsek.springaisamples.dto.WeatherResponse;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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