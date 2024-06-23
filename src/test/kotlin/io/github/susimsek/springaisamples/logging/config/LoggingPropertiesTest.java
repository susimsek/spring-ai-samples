package io.github.susimsek.springaisamples.logging.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.susimsek.springaisamples.logging.enums.HttpLogLevel;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
@SpringBootTest(properties = {
    "logging.http.enabled=false",
    "logging.http.level=FULL",
    "logging.http.obfuscate.maskValue=####",
    "logging.http.obfuscate.headers=Authorization,Set-Cookie",
    "logging.http.obfuscate.parameters=token,api_key",
    "logging.http.obfuscate.jsonBodyFields=$.password,$.token"
})
class LoggingPropertiesTest {

    @Configuration
    @EnableConfigurationProperties(LoggingProperties.class)
    static class TestConfig {
    }

    @Autowired
    private LoggingProperties loggingProperties;

    @Test
    void testConfiguredValues() {
        assertFalse(loggingProperties.getHttp().isEnabled());
        assertEquals(HttpLogLevel.FULL, loggingProperties.getHttp().getLogLevel());

        LoggingProperties.Obfuscate obfuscate = loggingProperties.getObfuscate();
        assertNotNull(obfuscate);
        assertEquals("####", obfuscate.getMaskValue());
        assertEquals(Arrays.asList("Authorization", "Set-Cookie"), obfuscate.getHeaders());
        assertEquals(Arrays.asList("token", "api_key"), obfuscate.getParameters());
        assertEquals(Arrays.asList("$.password", "$.token"), obfuscate.getJsonBodyFields());
    }
}