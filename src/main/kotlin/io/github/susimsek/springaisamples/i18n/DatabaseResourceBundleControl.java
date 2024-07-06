package io.github.susimsek.springaisamples.i18n;

import io.github.susimsek.springaisamples.service.MessageService;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseResourceBundleControl extends ResourceBundle.Control {

    private final MessageService messageService;

    @Override
    public List<Locale> getCandidateLocales(String baseName, Locale locale) {
        return Stream.of(locale, Locale.ROOT).toList();
    }

    @Override
    public ResourceBundle newBundle(String baseName,
                                    Locale locale,
                                    String format,
                                    ClassLoader loader,
                                    boolean reload) {
        return new DatabaseResourceBundle(locale, messageService);
    }
}