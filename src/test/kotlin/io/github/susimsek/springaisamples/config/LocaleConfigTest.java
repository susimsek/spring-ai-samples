package io.github.susimsek.springaisamples.config;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.context.support.ResourceBundleMessageSource;

class LocaleConfigTest {

    @Test
    void testMessageSource_BasenameSet() {
        // Given
        LocaleConfig localeConfig = new LocaleConfig();
        MessageSourceProperties properties = new MessageSourceProperties();
        properties.setBasename("i18n/messages");

        // When
        ResourceBundleMessageSource messageSource =
            (ResourceBundleMessageSource) localeConfig.messageSource(properties);

        // Then
        assertNotNull(messageSource);
        assertArrayEquals(new String[] {"i18n/messages"}, messageSource.getBasenameSet().toArray());
    }

    @Test
    void testMessageSource_EncodingSet() {
        // Given
        LocaleConfig localeConfig = new LocaleConfig();
        MessageSourceProperties properties = new MessageSourceProperties();
        properties.setEncoding(StandardCharsets.UTF_8);

        // When
        ResourceBundleMessageSource messageSource =
            (ResourceBundleMessageSource) localeConfig.messageSource(properties);

        // Then
        assertNotNull(messageSource);
    }

    @Test
    void testMessageSource_FallbackToSystemLocale() {
        // Given
        LocaleConfig localeConfig = new LocaleConfig();
        MessageSourceProperties properties = new MessageSourceProperties();
        properties.setFallbackToSystemLocale(false);

        // When
        ResourceBundleMessageSource messageSource =
            (ResourceBundleMessageSource) localeConfig.messageSource(properties);

        // Then
        assertNotNull(messageSource);
    }

    @Test
    void testMessageSource_CacheDurationSet() {
        // Given
        LocaleConfig localeConfig = new LocaleConfig();
        MessageSourceProperties properties = new MessageSourceProperties();
        properties.setCacheDuration(Duration.ofSeconds(60));

        // When
        ResourceBundleMessageSource messageSource =
            (ResourceBundleMessageSource) localeConfig.messageSource(properties);

        // Then
        assertNotNull(messageSource);
    }

    @Test
    void testMessageSource_AlwaysUseMessageFormat() {
        // Given
        LocaleConfig localeConfig = new LocaleConfig();
        MessageSourceProperties properties = new MessageSourceProperties();
        properties.setAlwaysUseMessageFormat(true);

        // When
        ResourceBundleMessageSource messageSource =
            (ResourceBundleMessageSource) localeConfig.messageSource(properties);

        // Then
        assertNotNull(messageSource);
    }

    @Test
    void testMessageSource_UseCodeAsDefaultMessage() {
        // Given
        LocaleConfig localeConfig = new LocaleConfig();
        MessageSourceProperties properties = new MessageSourceProperties();
        properties.setUseCodeAsDefaultMessage(true);

        // When
        ResourceBundleMessageSource messageSource =
            (ResourceBundleMessageSource) localeConfig.messageSource(properties);

        // Then
        assertNotNull(messageSource);
    }

    @Test
    void testMessageSource_NullSettings() {
        // Given
        LocaleConfig localeConfig = new LocaleConfig();
        MessageSourceProperties properties = new MessageSourceProperties();
        properties.setEncoding(null);
        properties.setBasename(null);
        properties.setCacheDuration(null);

        // When
        ResourceBundleMessageSource messageSource =
            (ResourceBundleMessageSource) localeConfig.messageSource(properties);

        // Then
        assertNotNull(messageSource);
    }
}
