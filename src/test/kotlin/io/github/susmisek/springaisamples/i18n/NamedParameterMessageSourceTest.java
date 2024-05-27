package io.github.susmisek.springaisamples.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Unit tests for {@link NamedParameterMessageSource}.
 */
class NamedParameterMessageSourceTest {

    private NamedParameterMessageSource namedParameterMessageSource;

    @BeforeEach
    public void setUp() {
        namedParameterMessageSource = new NamedParameterMessageSource();
        namedParameterMessageSource.setBasenames("i18n/messages");
        namedParameterMessageSource.setDefaultEncoding("UTF-8");
    }

    @Test
    void testGetMessageWithNamedArgs_en() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        Map<String, String> params = new HashMap<>();
        params.put("name", "John");
        params.put("day", "Monday");

        String message = namedParameterMessageSource.getMessageWithNamedArgs("welcome.message", params);
        assertEquals("Welcome, John! Today is Monday.", message);
    }

    @Test
    void testGetMessageWithNamedArgs_tr() {
        LocaleContextHolder.setLocale(new Locale("tr"));
        Map<String, String> params = new HashMap<>();
        params.put("name", "Ali");
        params.put("day", "Pazartesi");

        String message = namedParameterMessageSource.getMessageWithNamedArgs("welcome.message", params);
        assertEquals("Hoş geldiniz, Ali! Bugün Pazartesi.", message);
    }

    @Test
    void testGetMessageWithNamedArgsNoParams() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        Map<String, String> params = new HashMap<>();

        String message = namedParameterMessageSource.getMessageWithNamedArgs("welcome.message", params);
        assertEquals("Welcome, {name}! Today is {day}.", message);
    }

    @Test
    void testGetMessageWithNamedArgsNullParams() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);

        String message = namedParameterMessageSource.getMessageWithNamedArgs("welcome.message", null);
        assertEquals("Welcome, {name}! Today is {day}.", message);
    }

    @Test
    void testGetMessageWithNamedArgsMissingParams() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        Map<String, String> params = new HashMap<>();
        params.put("name", "John");

        String message = namedParameterMessageSource.getMessageWithNamedArgs("welcome.message", params);
        assertEquals("Welcome, John! Today is .", message);
    }

    @Test
    void testGetMessageWithNamedArgsLocale() {
        LocaleContextHolder.setLocale(Locale.FRANCE);
        Map<String, String> params = new HashMap<>();
        params.put("name", "Jean");
        params.put("day", "Lundi");

        String message = namedParameterMessageSource.getMessageWithNamedArgs(
            "welcome.message", params, new Locale("TR", "tr"));
        assertEquals("Hoş geldiniz, Jean! Bugün Lundi.", message);
    }

    @Test
    void testGetMessage() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        String message = namedParameterMessageSource.getMessage("welcome.message");
        assertEquals("Welcome, {name}! Today is {day}.", message);
    }
}