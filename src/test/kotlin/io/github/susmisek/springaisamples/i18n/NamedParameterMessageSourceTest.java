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

    private NamedParameterMessageSource namedParameterMessageSource;

    @BeforeEach
    void setUp() {
        namedParameterMessageSource = new NamedParameterMessageSource();
        namedParameterMessageSource.setBasenames("i18n/messages");
        namedParameterMessageSource.setUseCodeAsDefaultMessage(true);
        namedParameterMessageSource.setDefaultEncoding("UTF-8");
        namedParameterMessageSource.setFallbackToSystemLocale(false);
        namedParameterMessageSource.setCacheSeconds(3600);
    }

    @Test
    void testMessageWithNamedParameter() {
        // Tests retrieving a message with a named parameter
        String code = "hello.message";
        Map<String, String> namedParams = new HashMap<>();
        namedParams.put("name", "John");
        namedParameterMessageSource.setNamedParameters(namedParams);

        String expected = "Hello, John!";
        String actual = namedParameterMessageSource.getMessage(code);

        assertEquals(expected, actual);
    }

    @Test
    void testMessageWithoutNamedParameter() {
        // Tests retrieving a message without a named parameter
        String code = "hello.message";

        String expected = "Hello, {name}!";
        String actual = namedParameterMessageSource.getMessage(code);

        assertEquals(expected, actual);
    }

    @Test
    void testMessageWithMultipleNamedParameters() {
        // Tests retrieving a message with multiple named parameters
        String code = "greeting.message";
        Map<String, String> namedParams = new HashMap<>();
        namedParams.put("time", "morning");
        namedParams.put("name", "Jane");
        namedParameterMessageSource.setNamedParameters(namedParams);

        String expected = "Good morning, Jane!";
        String actual = namedParameterMessageSource.getMessage(code);

        assertEquals(expected, actual);
    }

    @Test
    void testMessageWithNonexistentParameter() {
        // Tests retrieving a message with a nonexistent parameter
        String code = "goodbye.message";

        String expected = "Goodbye, {name}!";
        String actual = namedParameterMessageSource.getMessage(code);

        assertEquals(expected, actual);
    }

    @Test
    void testMessageWithLocale() {
        // Tests retrieving a message with a specific locale
        String code = "hello.message";
        Map<String, String> namedParams = new HashMap<>();
        namedParams.put("name", "John");
        namedParameterMessageSource.setNamedParameters(namedParams);

        String expected = "Merhaba, John!";
        String actual = namedParameterMessageSource.getMessage(code, null, new Locale("tr", "TR"));

        assertEquals(expected, actual);
    }

    @Test
    void addNamedParameter_ShouldAddParameter_WhenCalled() {
        // Tests adding a named parameter correctly
        NamedParameterMessageSource localMessageSource = new NamedParameterMessageSource();
        localMessageSource.addNamedParameter("name", "John");

        Map<String, String> namedParameters = localMessageSource.getNamedParameters();
        assertEquals(1, namedParameters.size());
        assertEquals("John", namedParameters.get("name"));
    }

    @Test
    void testGetMessageWithNamedParametersAndLocale() {
        // Tests retrieving a message with named parameters and a specific locale
        String code = "error.validation";
        Map<String, String> params = new HashMap<>();
        params.put("field", "username");
        params.put("error", "must not be empty");

        String expectedMessageEn = "Validation error on field 'username': must not be empty";
        String actualMessageEn = namedParameterMessageSource.getMessageWithNamedArgs(code, params, Locale.ENGLISH);
        String expectedMessageTr = "Alan 'username' için doğrulama hatası: must not be empty";
        String actualMessageTr = namedParameterMessageSource.getMessageWithNamedArgs(code, params, new Locale("tr", "TR"));

        assertEquals(expectedMessageEn, actualMessageEn);
        assertEquals(expectedMessageTr, actualMessageTr);
    }

    @Test
    void testGetMessageWithNamedParameters() {
        // Tests retrieving a message with named parameters
        String code = "error.validation";
        Map<String, String> params = new HashMap<>();
        params.put("field", "username");
        params.put("error", "must not be empty");

        String expectedMessage = "Validation error on field 'username': must not be empty";
        String actualMessage = namedParameterMessageSource.getMessageWithNamedArgs(code, params);

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testGetMessageWithNamedArgs_MissingArgs() {
        // Tests retrieving a message with missing parameters
        String code = "hello.message";
        String message = "Hello, {name}!";
        Map<String, String> args = new HashMap<>();

        String result = namedParameterMessageSource.getMessageWithNamedArgs(code, args, Locale.ENGLISH);

        assertEquals(message, result);
    }

    @Test
    void testGetMessageWithNamedArgs_NoArgs() {
        // Tests retrieving a message with no parameters
        String code = "hello.message";
        String message = "Hello, World!";
        namedParameterMessageSource.addNamedParameter("name", "World");

        String result = namedParameterMessageSource.getMessageWithNamedArgs(code, null, Locale.ENGLISH);

        assertEquals(message, result);
    }

    @Test
    void testGetMessageInternalWithNullMessage() {
        // Tests getMessageInternal method with null message
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
        // Tests retrieving a message with empty named parameters
        namedParameterMessageSource.setNamedParameters(new ConcurrentHashMap<>());
        String result = namedParameterMessageSource.getMessageInternal("test.message", null, Locale.ENGLISH);
        assertEquals("This is {param1}", result);
    }

    @Test
    void testMessageWithNamedParametersEmpty() {
        // Tests retrieving a message with empty named parameters
        Map<String, String> emptyParams = new ConcurrentHashMap<>();
        namedParameterMessageSource.setNamedParameters(emptyParams);
        String result = namedParameterMessageSource.getMessageInternal("test.message", null, Locale.ENGLISH);
        assertEquals("This is {param1}", result);
    }

    @Test
    void testMessageWithoutNamedParameters() {
        // Tests retrieving a message without named parameters
        String result = namedParameterMessageSource.getMessageInternal("simple.message", null, Locale.ENGLISH);
        assertEquals("This is a simple message", result);
    }

    @Test
    void testMessageWithNoNamedParametersInMap() {
        // Tests retrieving a message with no named parameters in the map
        String result = namedParameterMessageSource.getMessageInternal("test.message", null, Locale.ENGLISH);
        assertEquals("This is {param1}", result);
    }

    @Test
    void testNamedParameterPattern() {
        // Tests the named parameter pattern
        Pattern pattern = Pattern.compile("\\{([a-zA-Z0-9]+)}");
        Matcher matcher = pattern.matcher("This is {param1} and {param2}");
        assertTrue(matcher.find());
        assertEquals("param1", matcher.group(1));
        assertTrue(matcher.find());
        assertEquals("param2", matcher.group(1));
    }

    @Test
    void testGetMessageInternal_NoNamedParameters() {
        // Tests getMessageInternal when there are no named parameters
        String code = "simple.message";
        namedParameterMessageSource.setNamedParameters(new ConcurrentHashMap<>());
        String result = namedParameterMessageSource.getMessageInternal(code, null, Locale.ENGLISH);
        assertEquals("This is a simple message", result);
    }

    @Test
    void testGetMessageInternal_MessageNull() {
        // Tests getMessageInternal when message is null
        ResourceBundleMessageSource mockMessageSource = new ResourceBundleMessageSource() {
            @Override
            protected String getMessageInternal(String code, Object[] args, Locale locale) {
                return null;
            }
        };

        NamedParameterMessageSource customMessageSource = new NamedParameterMessageSource();
        customMessageSource.setParentMessageSource(mockMessageSource);
        String result = customMessageSource.getMessageInternal("nonexistent.message", null, Locale.ENGLISH);
        assertNull(result);
    }

    @Test
    void testGetMessageInternal_NoNamedParametersButHasPlaceholders() {
        // Tests getMessageInternal when there are no named parameters but the message has placeholders
        namedParameterMessageSource.setNamedParameters(new ConcurrentHashMap<>());
        String code = "test.message.with.placeholders";
        String message = "This is {param1}";
        ResourceBundleMessageSource mockMessageSource = new ResourceBundleMessageSource() {
            @Override
            protected String getMessageInternal(String code, Object[] args, Locale locale) {
                return message;
            }
        };

        namedParameterMessageSource.setParentMessageSource(mockMessageSource);
        String result = namedParameterMessageSource.getMessageInternal(code, null, Locale.ENGLISH);
        assertEquals("This is {param1}", result);
    }

    @Test
    void testGetMessageInternal_MessageNonNullAndNamedParametersEmpty() {
        // Tests getMessageInternal when message is non-null and named parameters are empty
        namedParameterMessageSource.setNamedParameters(new ConcurrentHashMap<>());
        String code = "simple.message";
        String message = "This is a simple message";
        ResourceBundleMessageSource mockMessageSource = new ResourceBundleMessageSource() {
            @Override
            protected String getMessageInternal(String code, Object[] args, Locale locale) {
                return message;
            }
        };

        namedParameterMessageSource.setParentMessageSource(mockMessageSource);
        String result = namedParameterMessageSource.getMessageInternal(code, null, Locale.ENGLISH);
        assertEquals("This is a simple message", result);
    }

    @Test
    void testGetMessageInternal_MessageNullAndNamedParametersNonEmpty() {
        // Tests getMessageInternal when message is null and named parameters are non-empty
        namedParameterMessageSource.addNamedParameter("name", "John");
        String code = "nonexistent.message";
        ResourceBundleMessageSource mockMessageSource = new ResourceBundleMessageSource() {
            @Override
            protected String getMessageInternal(String code, Object[] args, Locale locale) {
                return null;
            }
        };

        namedParameterMessageSource.setParentMessageSource(mockMessageSource);
        String result = namedParameterMessageSource.getMessageInternal(code, null, Locale.ENGLISH);
        assertNull(result);
    }
}