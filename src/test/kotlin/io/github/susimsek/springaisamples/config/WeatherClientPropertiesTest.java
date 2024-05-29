package io.github.susimsek.springaisamples.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Locale;
import java.util.Set;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WeatherClientPropertiesTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ResourceBundleMessageInterpolator(
                locale -> java.util.ResourceBundle.getBundle("i18n/messages", locale)))
            .buildValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testWeatherClientPropertiesValid() {
        WeatherClientProperties properties = new WeatherClientProperties();
        properties.setApiKey("123456789012345678901234567890");
        properties.setApiUrl("https://api.example.com");

        Set<ConstraintViolation<WeatherClientProperties>> violations = validator.validate(properties);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testWeatherClientPropertiesInvalidApiKey() {
        WeatherClientProperties properties = new WeatherClientProperties();
        properties.setApiKey("invalidkey");
        properties.setApiUrl("https://api.example.com");

        Set<ConstraintViolation<WeatherClientProperties>> violations = validator.validate(properties);

        assertEquals(1, violations.size());

        ConstraintViolation<WeatherClientProperties> violation = violations.iterator().next();
        assertEquals("Field must be between 30 and 30 characters", violation.getMessage());
    }

    @Test
    void testWeatherClientPropertiesInvalidApiUrl() {
        WeatherClientProperties properties = new WeatherClientProperties();
        properties.setApiKey("123456789012345678901234567890");
        properties.setApiUrl("invalid-url");

        Set<ConstraintViolation<WeatherClientProperties>> violations = validator.validate(properties);

        assertEquals(1, violations.size());

        ConstraintViolation<WeatherClientProperties> violation = violations.iterator().next();
        assertEquals("Field must be a valid URL", violation.getMessage());
    }

    @Test
    void testWeatherClientPropertiesInvalidApiKeyInTurkish() {
        Locale originalLocale = Locale.getDefault();
        Locale.setDefault(new Locale("tr"));

        try {
            WeatherClientProperties properties = new WeatherClientProperties();
            properties.setApiKey("invalidkey");
            properties.setApiUrl("https://api.example.com");

            Set<ConstraintViolation<WeatherClientProperties>> violations = validator.validate(properties);

            assertEquals(1, violations.size());

            ConstraintViolation<WeatherClientProperties> violation = violations.iterator().next();
            assertEquals("Alan 30 ile 30 karakter arasında olmalıdır", violation.getMessage());
        } finally {
            Locale.setDefault(originalLocale);
        }
    }
}