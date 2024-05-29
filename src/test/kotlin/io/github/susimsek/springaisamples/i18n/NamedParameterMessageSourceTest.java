package io.github.susimsek.springaisamples.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.susimsek.springaisamples.i18n.NamedParameterMessageSource;
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
        messageSource = new NamedParameterMessageSource();
        messageSource.setBasename("i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setAlwaysUseMessageFormat(false);
        messageSource.setUseCodeAsDefaultMessage(false);
    }

    @Test
    void testGetMessageWithNamedArgs() {
        Map<String, String> args = new HashMap<>();
        args.put("name", "John");

        String result = messageSource.getMessageWithNamedArgs("greeting.named", args);
        assertEquals("Hello, John!", result);
    }

    @Test
    void testGetMessageWithNamedArgs_Locale() {
        Map<String, String> args = new HashMap<>();
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
        Map<String, String> args = new HashMap<>();
        args.put("firstName", "John");
        args.put("lastName", "Doe");

        String result = messageSource.getMessageWithNamedArgs("greeting.fullname", args);
        assertEquals("Hello, John Doe!", result);
    }

    @Test
    void testGetMessageWithNamedArgs_MultipleArgs_Locale() {
        Map<String, String> args = new HashMap<>();
        args.put("firstName", "John");
        args.put("lastName", "Doe");

        String result = messageSource.getMessageWithNamedArgs("greeting.fullname", args, Locale.ENGLISH);
        assertEquals("Hello, John Doe!", result);
    }

    @Test
    void testGetMessageWithNamedArgs_Turkish() {
        Map<String, String> args = new HashMap<>();
        args.put("name", "Ahmet");

        String result = messageSource.getMessageWithNamedArgs("greeting.named", args, new Locale("tr"));
        assertEquals("Merhaba, Ahmet!", result);
    }

    @Test
    void testGetMessageWithNamedArgs_MultipleArgs_Turkish() {
        Map<String, String> args = new HashMap<>();
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
        Map<String, String> args = new HashMap<>();
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
        Map<String, String> args = new HashMap<>();
        String result = messageSource.getMessageWithNamedArgs("greeting.named", args, Locale.ENGLISH);
        assertEquals("Hello, {name}!", result);
    }

    @Test
    void testReplaceNamedParameters_PartialArgs() {
        Map<String, String> args = new HashMap<>();
        args.put("firstName", "John");
        // lastName eksik

        String result = messageSource.getMessageWithNamedArgs("greeting.fullname", args, Locale.ENGLISH);
        assertEquals("Hello, John {lastName}!", result);
    }
}