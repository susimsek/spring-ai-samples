package io.github.susimsek.springaisamples.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.cache")
public class CacheProperties {
    private Map<String, CacheConfig> caches = new HashMap<>();

    @Getter
    @Setter
    public static class CacheConfig {
        private Duration ttl = Duration.ofHours(1);
    }
}