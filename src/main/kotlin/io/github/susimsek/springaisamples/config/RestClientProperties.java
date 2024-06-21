package io.github.susimsek.springaisamples.config;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.restclient")
public class RestClientProperties {

    private Duration connectTimeout;
    private Duration readTimeout;
}