package io.github.susimsek.springaisamples.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;


@Getter
@Setter
@ConfigurationProperties(prefix = "weather.client")
@Validated
public class WeatherClientProperties {

    @NotBlank(message = "{validation.field.notBlank}")
    @Size(min = 30, max = 30, message = "{validation.field.size}")
    private String apiKey;

    @NotBlank(message = "{validation.field.notBlank}")
    @URL(message = "{validation.field.url}")
    @Size(max = 256, message = "{validation.field.size}")
    String apiUrl;
}