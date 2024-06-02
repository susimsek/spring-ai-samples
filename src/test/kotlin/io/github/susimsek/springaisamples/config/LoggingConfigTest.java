package io.github.susimsek.springaisamples.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.susimsek.springaisamples.logging.config.LoggingProperties;
import io.github.susimsek.springaisamples.logging.formatter.JsonLogFormatter;
import io.github.susimsek.springaisamples.logging.formatter.LogFormatter;
import io.github.susimsek.springaisamples.logging.interceptor.RestClientLoggingInterceptor;
import io.github.susimsek.springaisamples.logging.strategy.DefaultObfuscationStrategy;
import io.github.susimsek.springaisamples.logging.strategy.NoOpObfuscationStrategy;
import io.github.susimsek.springaisamples.logging.strategy.ObfuscationStrategy;
import io.github.susimsek.springaisamples.logging.utils.Obfuscator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@ExtendWith(MockitoExtension.class)
class LoggingConfigTest {

    @Mock
    private LoggingProperties loggingProperties;

    @Mock
    private ObjectProvider<ObjectMapper> objectMapperProvider;

    @InjectMocks
    private LoggingConfig loggingConfig;

    @Test
    void testRestClientLoggingInterceptorBean() {
        LogFormatter logFormatter = mock(LogFormatter.class);
        Obfuscator obfuscator = mock(Obfuscator.class);

        RestClientLoggingInterceptor interceptor = loggingConfig.restClientLoggingInterceptor(loggingProperties, logFormatter, obfuscator);

        assertNotNull(interceptor);
        // Additional verifications for interceptor behavior can be added if needed
    }

    @Test
    void testLogFormatterBean() {
        ObjectMapper objectMapper = new ObjectMapper();
        when(objectMapperProvider.getIfAvailable(any())).thenReturn(objectMapper);

        LogFormatter logFormatter = loggingConfig.logFormatter(objectMapperProvider);

        assertNotNull(logFormatter);
        assertInstanceOf(JsonLogFormatter.class, logFormatter);
    }

    @Test
    void testObfuscationStrategyBeanWithDefaultObfuscation() {
        ObjectMapper objectMapper = new ObjectMapper();
        LoggingProperties.Obfuscate obfuscate = new LoggingProperties.Obfuscate();
        obfuscate.setEnabled(true);
        when(loggingProperties.getObfuscate()).thenReturn(obfuscate);

        ObfuscationStrategy obfuscationStrategy = loggingConfig.obfuscationStrategy(loggingProperties, objectMapper);

        assertNotNull(obfuscationStrategy);
        assertInstanceOf(DefaultObfuscationStrategy.class, obfuscationStrategy);
    }

    @Test
    void testObfuscationStrategyBeanWithNoOpObfuscation() {
        ObjectMapper objectMapper = new ObjectMapper();
        LoggingProperties.Obfuscate obfuscate = new LoggingProperties.Obfuscate();
        obfuscate.setEnabled(false);
        when(loggingProperties.getObfuscate()).thenReturn(obfuscate);

        ObfuscationStrategy obfuscationStrategy = loggingConfig.obfuscationStrategy(loggingProperties, objectMapper);

        assertNotNull(obfuscationStrategy);
        assertInstanceOf(NoOpObfuscationStrategy.class, obfuscationStrategy);
    }

    @Test
    void testObfuscatorBean() {
        ObfuscationStrategy obfuscationStrategy = mock(ObfuscationStrategy.class);

        Obfuscator obfuscator = loggingConfig.obfuscator(obfuscationStrategy);

        assertNotNull(obfuscator);
    }

    // Additional test to cover conditional property scenario
    @Test
    void testConditionalOnProperty() {
        // Simulate conditional property not being set
        System.clearProperty("logging.http.enabled");
        LoggingConfig config = new LoggingConfig();
        assertTrue(config.getClass().isAnnotationPresent(ConditionalOnProperty.class));

        ConditionalOnProperty conditionalOnProperty = config.getClass().getAnnotation(ConditionalOnProperty.class);
        assertEquals("logging.http.enabled", conditionalOnProperty.name()[0]);
        assertEquals("true", conditionalOnProperty.havingValue());
        assertTrue(conditionalOnProperty.matchIfMissing());
    }
}