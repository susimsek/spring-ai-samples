package io.github.susimsek.springaisamples.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.susimsek.springaisamples.logging.config.LoggingProperties;
import io.github.susimsek.springaisamples.logging.formatter.JsonLogFormatter;
import io.github.susimsek.springaisamples.logging.formatter.LogFormatter;
import io.github.susimsek.springaisamples.logging.interceptor.RestClientLoggingInterceptor;
import io.github.susimsek.springaisamples.logging.utils.Obfuscator;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
@SpringBootTest
@Import(LoggingConfigTest.TestConfig.class)
class LoggingConfigTest {

    @Configuration
    @Import(LoggingConfig.class)
    static class TestConfig {
        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @Autowired
    private LoggingProperties loggingProperties;

    @Autowired
    private RestClientLoggingInterceptor restClientLoggingInterceptor;

    @Autowired
    private LogFormatter logFormatter;

    @Autowired
    private Obfuscator obfuscator;

    @Test
    void testBeansLoaded() {
        assertNotNull(loggingProperties);
        assertNotNull(restClientLoggingInterceptor);
        assertNotNull(logFormatter);
        assertNotNull(obfuscator);
    }

    @Test
    void testRestClientLoggingInterceptor() throws Exception {
        Field loggingPropertiesField = RestClientLoggingInterceptor.class.getDeclaredField("loggingProperties");
        loggingPropertiesField.setAccessible(true);
        LoggingProperties interceptorLoggingProperties = (LoggingProperties) loggingPropertiesField.get(restClientLoggingInterceptor);

        Field logFormatterField = RestClientLoggingInterceptor.class.getDeclaredField("logFormatter");
        logFormatterField.setAccessible(true);
        LogFormatter interceptorLogFormatter = (LogFormatter) logFormatterField.get(restClientLoggingInterceptor);

        Field obfuscatorField = RestClientLoggingInterceptor.class.getDeclaredField("obfuscator");
        obfuscatorField.setAccessible(true);
        Obfuscator interceptorObfuscator = (Obfuscator) obfuscatorField.get(restClientLoggingInterceptor);

        assertEquals(loggingProperties, interceptorLoggingProperties);
        assertEquals(logFormatter, interceptorLogFormatter);
        assertEquals(obfuscator, interceptorObfuscator);
    }

    @Test
    void testLogFormatter() throws Exception {
        assertInstanceOf(JsonLogFormatter.class, logFormatter);

        Field objectMapperField = JsonLogFormatter.class.getDeclaredField("objectMapper");
        objectMapperField.setAccessible(true);
        ObjectMapper formatterObjectMapper = (ObjectMapper) objectMapperField.get(logFormatter);

        assertNotNull(formatterObjectMapper);
    }

    @Test
    void testObfuscator() throws Exception {
        Field loggingPropertiesField = Obfuscator.class.getDeclaredField("loggingProperties");
        loggingPropertiesField.setAccessible(true);
        LoggingProperties obfuscatorLoggingProperties = (LoggingProperties) loggingPropertiesField.get(obfuscator);

        Field objectMapperField = Obfuscator.class.getDeclaredField("objectMapper");
        objectMapperField.setAccessible(true);
        ObjectMapper obfuscatorObjectMapper = (ObjectMapper) objectMapperField.get(obfuscator);

        assertEquals(loggingProperties, obfuscatorLoggingProperties);
        assertNotNull(obfuscatorObjectMapper);
    }
}