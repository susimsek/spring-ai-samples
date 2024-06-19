package io.github.susimsek.springaisamples.config;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CacheProperties.class)
@RequiredArgsConstructor
@EnableCaching
public class CacheConfig {

    private final CacheProperties cacheProperties;

    @Bean
    public CacheManager cacheManager() {
        return new CustomConcurrentMapCacheManager(cacheProperties.getCaches());
    }

    static class CustomConcurrentMapCacheManager extends ConcurrentMapCacheManager {
        private final Map<String, CacheProperties.CacheConfig> cacheConfigMap;

        public CustomConcurrentMapCacheManager(Map<String, CacheProperties.CacheConfig> cacheConfigMap) {
            super();
            this.cacheConfigMap = cacheConfigMap;
            setCacheNames(cacheConfigMap.keySet());
        }

        @Override
        @NonNull
        protected Cache createConcurrentMapCache(@NonNull final String name) {
            CacheProperties.CacheConfig config = cacheConfigMap.getOrDefault(name, cacheConfigMap.get("default"));
            return new CustomConcurrentMapCache(name, new ConcurrentHashMap<>(), config.getTtl());
        }
    }

    static class CustomConcurrentMapCache extends org.springframework.cache.concurrent.ConcurrentMapCache {
        private final long ttl;
        private final ConcurrentMap<Object, Long> expirationMap = new ConcurrentHashMap<>();

        public CustomConcurrentMapCache(String name, ConcurrentMap<Object, Object> store, Duration ttl) {
            super(name, store, true);
            this.ttl = ttl.toMillis();
        }

        @Override
        public void put(@NonNull Object key, Object value) {
            super.put(key, value);
            expirationMap.put(key, System.currentTimeMillis() + ttl);
        }

        @Override
        public ValueWrapper get(@NonNull Object key) {
            if (isExpired(key)) {
                evict(key);
                return null;
            }
            return super.get(key);
        }

        private boolean isExpired(Object key) {
            Long expirationTime = expirationMap.get(key);
            return expirationTime == null || System.currentTimeMillis() > expirationTime;
        }
    }
}