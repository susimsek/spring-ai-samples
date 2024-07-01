package io.github.susimsek.springaisamples.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "spring.cache")
public class CacheProperties {
    @Valid
    private Map<String, CacheConfig> caches = new HashMap<>();

    @Getter
    @Setter
    public static class CacheConfig {
        @NotNull(message = "{validation.field.notNull}")
        private Duration ttl = Duration.ofHours(1);
    }
}