package io.github.susimsek.springaisamples.i18n;

import io.github.susimsek.springaisamples.service.MessageService;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import org.springframework.lang.NonNull;

public class DatabaseResourceBundle extends ResourceBundle {

    private final Locale locale;
    private final MessageService messageService;

    public DatabaseResourceBundle(Locale locale, MessageService messageService) {
        this.locale = locale;
        this.messageService = messageService;
    }

    @Override
    protected Object handleGetObject(@NonNull String key) {
        return getMessages().get(key);
    }

    @NonNull
    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(getMessages().keySet());
    }

    private Map<String, String> getMessages() {
        return messageService.getMessages(locale.getLanguage());
    }
}