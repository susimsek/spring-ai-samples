package io.github.susmisek.springaisamples.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NamedParameterMessageSourceTest {

    private NamedParameterMessageSource messageSource;

    @BeforeEach
    void setUp() {
        messageSource = new NamedParameterMessageSource();
        messageSource.setBasenames("i18n/messages");
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setCacheSeconds(3600);
    }

    @Test
    void testMessageWithNamedParameter() {
        String code = "hello.message";
        Map<String, String> namedParams = new HashMap<>();
        namedParams.put("name", "John");
        messageSource.setNamedParameters(namedParams);

        String expected = "Hello, John!";
        String actual = messageSource.getMessage(code);
        assertEquals(expected, actual);
    }

    @Test
    void testMessageWithoutNamedParameter() {
        String code = "hello.message";

        String expected = "Hello, {name}!";
        String actual = messageSource.getMessage(code);
        assertEquals(expected, actual);
    }

    @Test
    void testMessageWithMultipleNamedParameters() {
        String code = "greeting.message";
        Map<String, String> namedParams = new HashMap<>();
        namedParams.put("time", "morning");
        namedParams.put("name", "Jane");
        messageSource.setNamedParameters(namedParams);

        String expected = "Good morning, Jane!";
        String actual = messageSource.getMessage(code);
        assertEquals(expected, actual);
    }

    @Test
    void testMessageWithNonexistentParameter() {
        String code = "goodbye.message";

        String expected = "Goodbye, {name}!";
        String actual = messageSource.getMessage(code);
        assertEquals(expected, actual);
    }

    @Test
    void testMessageWithLocale() {
        String code = "hello.message";
        Map<String, String> namedParams = new HashMap<>();
        namedParams.put("name", "John");
        messageSource.setNamedParameters(namedParams);

        String expected = "Merhaba, John!";
        String actual = messageSource.getMessage(code, null, new Locale("tr", "TR"));
        assertEquals(expected, actual);
    }

    @Test
    void addNamedParameter_ShouldAddParameter_WhenCalled() {
        // Arrange
        NamedParameterMessageSource messageSource = new NamedParameterMessageSource();

        // Act
        messageSource.addNamedParameter("name", "John");

        // Assert
        Map<String, String> namedParameters = messageSource.getNamedParameters();
        assertEquals(1, namedParameters.size());
        assertEquals("John", namedParameters.get("name"));
    }
}
