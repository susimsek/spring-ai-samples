package io.github.susimsek.springaisamples.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.NoSuchMessageException;

class NamedParameterMessageSourceTest {

    private NamedParameterMessageSource messageSource;

    @BeforeEach
    void setUp() {
        messageSource = new NamedParameterMessageSource(null);
        messageSource.setAlwaysUseMessageFormat(false);
        messageSource.setUseCodeAsDefaultMessage(false);
    }

    @Test
    void testGetMessageWithNamedArgs() {
        Map<String, Object> args = new HashMap<>();
        args.put("name", "John");

        String result = messageSource.getMessageWithNamedArgs("greeting.named", args);
        assertEquals("Hello, John!", result);
    }

    @Test
    void testGetMessageWithNamedArgs_Locale() {
        Map<String, Object> args = new HashMap<>();
        args.put("name", "John");

        String result = messageSource.getMessageWithNamedArgs("greeting.named", args, Locale.ENGLISH);
        assertEquals("Hello, John!", result);
    }

    @Test
    void testGetMessageWithNamedArgs_NoArgs() {
        String result = messageSource.getMessageWithNamedArgs("greeting.simple", null);
        assertEquals("Hello, World!", result);
    }

    @Test
    void testGetMessageWithNamedArgs_NoArgs_Locale() {
        String result = messageSource.getMessageWithNamedArgs("greeting.simple", null, Locale.ENGLISH);
        assertEquals("Hello, World!", result);
    }

    @Test
    void testGetMessageWithNamedArgs_MultipleArgs() {
        Map<String, Object> args = new HashMap<>();
        args.put("firstName", "John");
        args.put("lastName", "Doe");

        String result = messageSource.getMessageWithNamedArgs("greeting.fullname", args);
        assertEquals("Hello, John Doe!", result);
    }

    @Test
    void testGetMessageWithNamedArgs_MultipleArgs_Locale() {
        Map<String, Object> args = new HashMap<>();
        args.put("firstName", "John");
        args.put("lastName", "Doe");

        String result = messageSource.getMessageWithNamedArgs("greeting.fullname", args, Locale.ENGLISH);
        assertEquals("Hello, John Doe!", result);
    }

    @Test
    void testGetMessageWithNamedArgs_Turkish() {
        Map<String, Object> args = new HashMap<>();
        args.put("name", "Ahmet");

        String result = messageSource.getMessageWithNamedArgs("greeting.named", args, new Locale("tr"));
        assertEquals("Merhaba, Ahmet!", result);
    }

    @Test
    void testGetMessageWithNamedArgs_MultipleArgs_Turkish() {
        Map<String, Object> args = new HashMap<>();
        args.put("firstName", "Ahmet");
        args.put("lastName", "Yılmaz");

        String result = messageSource.getMessageWithNamedArgs("greeting.fullname", args, new Locale("tr"));
        assertEquals("Merhaba, Ahmet Yılmaz!", result);
    }

    @Test
    void testGetMessage() {
        String result = messageSource.getMessage("greeting.simple");
        assertNotNull(result);
        assertEquals("Hello, World!", result);
    }

    @Test
    void testGetMessage_MissingMessage() {
        assertThrows(NoSuchMessageException.class, () -> {
            messageSource.getMessage("missing.message", null, Locale.ENGLISH);
        });
    }

    @Test
    void testGetMessageWithNamedArgs_MissingMessage() {
        Map<String, Object> args = new HashMap<>();
        args.put("name", "John");

        assertThrows(NoSuchMessageException.class, () -> {
            messageSource.getMessageWithNamedArgs("missing.message", args, Locale.ENGLISH);
        });
    }

    @Test
    void testGetMessageWithNamedArgs_NullArgs() {
        String result = messageSource.getMessageWithNamedArgs("greeting.named", null, Locale.ENGLISH);
        assertEquals("Hello, {name}!", result);
    }

    @Test
    void testGetMessageWithNamedArgs_EmptyArgs() {
        Map<String, Object> args = new HashMap<>();
        String result = messageSource.getMessageWithNamedArgs("greeting.named", args, Locale.ENGLISH);
        assertEquals("Hello, {name}!", result);
    }

    @Test
    void testReplaceNamedParameters_PartialArgs() {
        Map<String, Object> args = new HashMap<>();
        args.put("firstName", "John");
        // lastName eksik

        String result = messageSource.getMessageWithNamedArgs("greeting.fullname", args, Locale.ENGLISH);
        assertEquals("Hello, John {lastName}!", result);
    }

    @Test
    void testGetMessagesStartingWith() {
        Locale locale = Locale.ENGLISH;

        Map<String, String> messages = messageSource.getMessagesStartingWith("api-docs", locale);
        assertNotNull(messages);
        assertEquals("API Documentation Title", messages.get("api-docs.title"));
        assertEquals("API Documentation Description", messages.get("api-docs.description"));
    }

    @Test
    void testGetMessagesStartingWith_NoMatchingKeys() {
        Locale locale = Locale.ENGLISH;

        Map<String, String> messages = messageSource.getMessagesStartingWith("nonexistent-prefix", locale);
        assertNotNull(messages);
        assertTrue(messages.isEmpty());
    }

    @Test
    void testGetMessagesStartingWith_EmptyPrefix() {
        Locale locale = Locale.ENGLISH;

        Map<String, String> messages = messageSource.getMessagesStartingWith("", locale);
        assertNotNull(messages);
        // Assuming i18n/messages contains more than 2 entries
        assertTrue(messages.size() > 2);
    }

    @Test
    void testGetMessageWithNamedArgs_Integer() {
        Map<String, Object> args = new HashMap<>();
        args.put("min", 5);
        args.put("max", 20);

        String result = messageSource.getMessageWithNamedArgs("validation.field.size", args, Locale.ENGLISH);
        assertEquals("Field must be between 5 and 20 characters", result);
    }
}