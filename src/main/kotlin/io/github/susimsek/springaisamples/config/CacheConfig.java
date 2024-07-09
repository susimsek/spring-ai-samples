package io.github.susimsek.springaisamples.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.jcache.configuration.CaffeineConfiguration;
import com.github.benmanes.caffeine.jcache.spi.CaffeineCachingProvider;
import io.github.susimsek.springaisamples.constant.CacheName;
import lombok.RequiredArgsConstructor;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;

@Configuration(proxyBeanMethods = false)
@EnableCaching
@RequiredArgsConstructor
@EnableConfigurationProperties(CacheProperties.class)
public class CacheConfig {

    private final CacheProperties cacheProperties;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(buildCaffeineConfig(cacheProperties.getDefaultConfig()));
        return cacheManager;
    }

    @Bean
    @ConditionalOnProperty(name = "spring.jpa.properties.hibernate.cache.use_second_level_cache",
        havingValue = "true", matchIfMissing = true)
    public javax.cache.CacheManager jcacheManager(JCacheManagerCustomizer jcacheManagerCustomizer) {
        CachingProvider cachingProvider = Caching.getCachingProvider(
            CaffeineCachingProvider.class.getName());
        var cacheManager = cachingProvider.getCacheManager();
        jcacheManagerCustomizer.customize(cacheManager);
        return cacheManager;
    }

    @Bean
    @ConditionalOnProperty(name = "spring.jpa.properties.hibernate.cache.use_second_level_cache",
        havingValue = "true")
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager jcacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, jcacheManager);
    }

    @Bean
    @ConditionalOnProperty(name = "spring.jpa.properties.hibernate.cache.use_second_level_cache",
        havingValue = "true")
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        var hibernateCacheConfig = cacheProperties.getHibernate();
        return cm -> {
            createJCache(cm, CacheName.DEFAULT_UPDATE_TIMESTAMPS_REGION,
                hibernateCacheConfig.getDefaultUpdateTimestampsRegion());
            createJCache(cm, CacheName.DEFAULT_QUERY_RESULTS_REGION,
                hibernateCacheConfig.getDefaultQueryResultsRegion());
            cacheProperties.getCacheNames().forEach(cacheName -> {
                CacheProperties.CacheConfig config = cacheProperties.getCaches()
                    .getOrDefault(cacheName, cacheProperties.getDefaultConfig());
                createJCache(cm, cacheName, config);
            });
        };
    }

    private void createJCache(javax.cache.CacheManager cm,
                              String cacheName, CacheProperties.CacheConfig config) {
        CaffeineConfiguration<Object, Object> caffeineConfiguration = new CaffeineConfiguration<>();
        caffeineConfiguration.setMaximumSize(OptionalLong.of(config.getMaximumSize()));
        caffeineConfiguration.setExpireAfterWrite(OptionalLong.of(
            TimeUnit.SECONDS.toNanos(config.getTtl().getSeconds())));
        caffeineConfiguration.setStatisticsEnabled(true);
        if (config.getRefreshAfterWrite() != null) {
            caffeineConfiguration.setRefreshAfterWrite(
                OptionalLong.of(TimeUnit.SECONDS.toNanos(
                    config.getRefreshAfterWrite().getSeconds())));
        }
        caffeineConfiguration.setStatisticsEnabled(true);

        cm.createCache(cacheName, caffeineConfiguration);
    }

    private Caffeine<Object, Object> buildCaffeineConfig(CacheProperties.CacheConfig config) {
        var caffeineBuilder = Caffeine.newBuilder()
            .expireAfterWrite(config.getTtl())
            .initialCapacity(config.getInitialCapacity())
            .maximumSize(config.getMaximumSize())
            .recordStats();
        if (config.getRefreshAfterWrite() != null) {
            caffeineBuilder.refreshAfterWrite(config.getRefreshAfterWrite());
        }
        return caffeineBuilder;
    }
}