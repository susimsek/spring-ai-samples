package io.github.susimsek.springaisamples.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.susimsek.springaisamples.logging.config.LoggingProperties;
import io.github.susimsek.springaisamples.logging.formatter.JsonLogFormatter;
import io.github.susimsek.springaisamples.logging.formatter.LogFormatter;
import io.github.susimsek.springaisamples.logging.interceptor.RestClientLoggingInterceptor;
import io.github.susimsek.springaisamples.logging.utils.DefaultObfuscationStrategy;
import io.github.susimsek.springaisamples.logging.utils.NoOpObfuscationStrategy;
import io.github.susimsek.springaisamples.logging.utils.ObfuscationStrategy;
import io.github.susimsek.springaisamples.logging.utils.Obfuscator;
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
    public RestClientLoggingInterceptor restClientLoggingInterceptor(
        LoggingProperties loggingProperties, LogFormatter logFormatter, Obfuscator obfuscator) {
        return new RestClientLoggingInterceptor(loggingProperties, logFormatter, obfuscator);
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