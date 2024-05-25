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

    @Test
    void testGetMessageWithNamedParametersAndLocale() {
        String code = "error.validation";
        Map<String, String> params = new HashMap<>();
        params.put("field", "username");
        params.put("error", "must not be empty");
        String expectedMessageEn = "Validation error on field 'username': must not be empty";
        String actualMessageEn = messageSource.getMessageWithNamedArgs(code, params, Locale.ENGLISH);
        assertEquals(expectedMessageEn, actualMessageEn);

        String expectedMessageTr = "Alan 'username' için doğrulama hatası: must not be empty";
        String actualMessageTr = messageSource.getMessageWithNamedArgs(code, params, new Locale("TR","tr"));
        assertEquals(expectedMessageTr, actualMessageTr);
    }

    @Test
    void testGetMessageWithNamedParameters() {
        String code = "error.validation";
        Map<String, String> params = new HashMap<>();
        params.put("field", "username");
        params.put("error", "must not be empty");
        String expectedMessage = "Validation error on field 'username': must not be empty";
        String actualMessage = messageSource.getMessageWithNamedArgs(code, params);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testGetMessageWithNamedArgs_MissingArgs() {
        String code = "hello.message";
        String message = "Hello, {name}!";
        Map<String, String> args = new HashMap<>();
        String result = messageSource.getMessageWithNamedArgs(code, args, Locale.ENGLISH);
        assertEquals(message, result);
    }

    @Test
    void testGetMessageWithNamedArgs_NoArgs() {
        String code = "hello.message";
        String message = "Hello, World!";
        messageSource.addNamedParameter("name", "World");
        String result = messageSource.getMessageWithNamedArgs(code, null, Locale.ENGLISH);
        assertEquals(message, result);
    }
}
