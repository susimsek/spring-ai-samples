package io.github.susimsek.springaisamples.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
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
    private CacheConfig defaultConfig;

    private List<String> cacheNames;

    @Valid
    private Map<String, CacheConfig> caches = new HashMap<>();

    @Valid
    private HibernateCacheConfig hibernate;

    @Getter
    @Setter
    public static class CacheConfig {
        @NotNull(message = "{validation.field.notNull}")
        private Duration ttl = Duration.ofHours(1);

        @NotNull(message = "{validation.field.notNull}")
        private Integer initialCapacity = 1000;

        @NotNull(message = "{validation.field.notNull}")
        private Long maximumSize = 10000L;


        private Duration refreshAfterWrite;
    }

    @Getter
    @Setter
    public static class HibernateCacheConfig {
        @Valid
        private CacheConfig defaultUpdateTimestampsRegion;

        @Valid
        private CacheConfig defaultQueryResultsRegion;
    }
}