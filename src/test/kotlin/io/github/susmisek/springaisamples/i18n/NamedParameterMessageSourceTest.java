package io.github.susmisek.springaisamples.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

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
        // Given
        String code = "hello.message";
        Map<String, String> namedParams = new HashMap<>();
        namedParams.put("name", "John");
        messageSource.setNamedParameters(namedParams);

        // When
        String expected = "Hello, John!";
        String actual = messageSource.getMessage(code);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void testMessageWithoutNamedParameter() {
        // Given
        String code = "hello.message";

        // When
        String expected = "Hello, {name}!";
        String actual = messageSource.getMessage(code);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void testMessageWithMultipleNamedParameters() {
        // Given
        String code = "greeting.message";
        Map<String, String> namedParams = new HashMap<>();
        namedParams.put("time", "morning");
        namedParams.put("name", "Jane");
        messageSource.setNamedParameters(namedParams);

        // When
        String expected = "Good morning, Jane!";
        String actual = messageSource.getMessage(code);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void testMessageWithNonexistentParameter() {
        // Given
        String code = "goodbye.message";

        // When
        String expected = "Goodbye, {name}!";
        String actual = messageSource.getMessage(code);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void testMessageWithLocale() {
        // Given
        String code = "hello.message";
        Map<String, String> namedParams = new HashMap<>();
        namedParams.put("name", "John");
        messageSource.setNamedParameters(namedParams);

        // When
        String expected = "Merhaba, John!";
        String actual = messageSource.getMessage(code, null, new Locale("tr", "TR"));

        // Then
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

    @Test
    void testGetMessageWithNamedParametersAndLocale() {
        // Given
        String code = "error.validation";
        Map<String, String> params = new HashMap<>();
        params.put("field", "username");
        params.put("error", "must not be empty");

        // When
        String expectedMessageEn = "Validation error on field 'username': must not be empty";
        String actualMessageEn = messageSource.getMessageWithNamedArgs(code, params, Locale.ENGLISH);
        String expectedMessageTr = "Alan 'username' için doğrulama hatası: must not be empty";
        String actualMessageTr = messageSource.getMessageWithNamedArgs(code, params, new Locale("TR","tr"));

        // Then
        assertEquals(expectedMessageEn, actualMessageEn);
        assertEquals(expectedMessageTr, actualMessageTr);
    }

    @Test
    void testGetMessageWithNamedParameters() {
        // Given
        String code = "error.validation";
        Map<String, String> params = new HashMap<>();
        params.put("field", "username");
        params.put("error", "must not be empty");

        // When
        String expectedMessage = "Validation error on field 'username': must not be empty";
        String actualMessage = messageSource.getMessageWithNamedArgs(code, params);

        // Then
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testGetMessageWithNamedArgs_MissingArgs() {
        // Given
        String code = "hello.message";
        String message = "Hello, {name}!";
        Map<String, String> args = new HashMap<>();

        // When
        String result = messageSource.getMessageWithNamedArgs(code, args, Locale.ENGLISH);

        // Then
        assertEquals(message, result);
    }

    @Test
    void testGetMessageWithNamedArgs_NoArgs() {
        // Given
        String code = "hello.message";
        String message = "Hello, World!";
        messageSource.addNamedParameter("name", "World");

        // When
        String result = messageSource.getMessageWithNamedArgs(code, null, Locale.ENGLISH);

        // Then
        assertEquals(message, result);
    }

    @Test
    void testGetMessageInternalWithNullMessage() {
        ResourceBundleMessageSource mockMessageSource = new ResourceBundleMessageSource() {
            @Override
            protected String getMessageInternal(String code, Object[] args, Locale locale) {
                return null;
            }
        };

        NamedParameterMessageSource customMessageSource = new NamedParameterMessageSource();
        customMessageSource.setParentMessageSource(mockMessageSource);
        String result = customMessageSource.getMessageInternal("test.message", null, Locale.ENGLISH);
        assertNull(result);
    }

    @Test
    void testGetMessageWithEmptyNamedParameters() {
        messageSource.setNamedParameters(new ConcurrentHashMap<>());
        String result = messageSource.getMessageInternal("test.message", null, Locale.ENGLISH);
        assertEquals("This is {param1}", result);
    }

    @Test
    void testMessageWithNamedParametersEmpty() {
        Map<String, String> emptyParams = new ConcurrentHashMap<>();
        messageSource.setNamedParameters(emptyParams);
        String result = messageSource.getMessageInternal("test.message", null, Locale.ENGLISH);
        assertEquals("This is {param1}", result);
    }

    @Test
    void testMessageWithoutNamedParameters() {
        String result = messageSource.getMessageInternal("simple.message", null, Locale.ENGLISH);
        assertEquals("This is a simple message", result);
    }

    @Test
    void testMessageWithNoNamedParametersInMap() {
        String result = messageSource.getMessageInternal("test.message", null, Locale.ENGLISH);
        assertEquals("This is {param1}", result);
    }

    @Test
    void testNamedParameterPattern() {
        Pattern pattern = Pattern.compile("\\{([a-zA-Z0-9]+)}");
        Matcher matcher = pattern.matcher("This is {param1} and {param2}");
        assertTrue(matcher.find());
        assertEquals("param1", matcher.group(1));
        assertTrue(matcher.find());
        assertEquals("param2", matcher.group(1));
    }
}
