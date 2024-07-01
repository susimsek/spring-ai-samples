package io.github.susimsek.springaisamples.config;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "spring.restclient")
public class RestClientProperties {

    @NotNull(message = "{validation.field.notNull}")
    private Duration connectTimeout;

    @NotNull(message = "{validation.field.notNull}")
    private Duration readTimeout;
}