package io.github.susimsek.springaisamples.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableCaching
@RequiredArgsConstructor
@EnableConfigurationProperties(CacheProperties.class)
public class CacheConfig {

    private final CacheProperties cacheProperties;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        Map<String, CacheProperties.CacheConfig> caches = cacheProperties.getCaches();
        caches.forEach((name, config) -> {
            CacheProperties.CacheConfig cacheConfig = caches.getOrDefault(name, caches.get("default"));
            var caffeine = Caffeine.newBuilder()
                .expireAfterWrite(cacheConfig.getTtl());
            cacheManager.setCaffeine(caffeine);
        });
        return cacheManager;
    }
}