package io.github.susimsek.springaisamples.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.susimsek.springaisamples.enums.FilterOrder;
import io.github.susimsek.springaisamples.logging.aspect.LoggingAspect;
import io.github.susimsek.springaisamples.logging.config.LoggingProperties;
import io.github.susimsek.springaisamples.logging.filter.LoggingFilter;
import io.github.susimsek.springaisamples.logging.formatter.JsonLogFormatter;
import io.github.susimsek.springaisamples.logging.formatter.LogFormatter;
import io.github.susimsek.springaisamples.logging.handler.HttpLoggingHandler;
import io.github.susimsek.springaisamples.logging.handler.LoggingHandler;
import io.github.susimsek.springaisamples.logging.strategy.DefaultObfuscationStrategy;
import io.github.susimsek.springaisamples.logging.strategy.NoOpObfuscationStrategy;
import io.github.susimsek.springaisamples.logging.strategy.ObfuscationStrategy;
import io.github.susimsek.springaisamples.logging.utils.Obfuscator;
import io.github.susimsek.springaisamples.logging.wrapper.HttpLoggingWrapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LoggingProperties.class)
@ConditionalOnProperty(name = "logging.http.enabled", havingValue = "true", matchIfMissing = true)
public class LoggingConfig {

    @Bean
    public HttpLoggingWrapper httpLoggingWrapper(LoggingHandler loggingHandler) {
        return new HttpLoggingWrapper(loggingHandler);
    }

    @Bean
    public LoggingFilter loggingFilter(LoggingHandler loggingHandler) {
        return new LoggingFilter(loggingHandler);
    }

    @Bean
    @ConditionalOnProperty(name = "logging.aspect.enabled", havingValue = "true", matchIfMissing = true)
    public LoggingAspect loggingAspect(LoggingHandler loggingHandler) {
        return new LoggingAspect(loggingHandler);
    }

    @Bean
    public LoggingHandler loggingHandler(LoggingProperties loggingProperties,
                                         LogFormatter logFormatter,
                                         Obfuscator obfuscator,
                                         RequestMatchersConfig requestMatchersConfig) {
        return HttpLoggingHandler.builder(logFormatter, obfuscator)
            .httpLogLevel(loggingProperties.getHttp().getLogLevel())
            .methodLogLevel(loggingProperties.getAspect().getLogLevel())
            .order(FilterOrder.LOGGING.order())
            .requestMatchers(requestMatchersConfig.staticResourcePaths()).permitAll()
            .requestMatchers(requestMatchersConfig.swaggerResourcePaths()).permitAll()
            .requestMatchers(requestMatchersConfig.actuatorEndpoints()).permitAll()
            .anyRequest().logged()
            .build();
    }

    @Bean
    public LogFormatter logFormatter(ObjectProvider<ObjectMapper> objectMapperProvider) {
        ObjectMapper objectMapper = objectMapperProvider.getIfAvailable(ObjectMapper::new);
        return new JsonLogFormatter(objectMapper);
    }

    @Bean
    public ObfuscationStrategy obfuscationStrategy(LoggingProperties loggingProperties,
                                                   ObjectMapper objectMapper) {
        if (loggingProperties.getObfuscate().isEnabled()) {
            return new DefaultObfuscationStrategy(loggingProperties, objectMapper);
        } else {
            return new NoOpObfuscationStrategy();
        }
    }

    @Bean
    public Obfuscator obfuscator(ObfuscationStrategy obfuscationStrategy) {
        return new Obfuscator(obfuscationStrategy);
    }
}