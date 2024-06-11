package io.github.susimsek.springaisamples.customizer;

import io.swagger.v3.oas.models.OpenAPI;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@RequiredArgsConstructor
public class OpenApiLocalizationCustomizer implements OpenApiCustomizer {

    private static final Pattern MESSAGE_PATTERN = Pattern.compile("\\{(.+?)}");
    private final MessageSource messageSource;

    @Override
    public void customise(OpenAPI openApi) {
        var locale = LocaleContextHolder.getLocale();
        customizeOpenApiInfo(openApi, locale);
        customizeOperations(openApi);
    }

    private void customizeOpenApiInfo(OpenAPI openApi, Locale locale) {
        openApi.getInfo()
            .title(getMessage("api-docs.title", locale))
            .description(getMessage("api-docs.description", locale))
            .contact(new io.swagger.v3.oas.models.info.Contact()
                .name(getMessage("api-docs.contact.name", locale))
                .email(getMessage("api-docs.contact.email", locale))
                .url(getMessage("api-docs.contact.url", locale)))
            .license(new io.swagger.v3.oas.models.info.License()
                .name(getMessage("api-docs.license.name", locale))
                .url(getMessage("api-docs.license.url", locale)));
    }

    private void customizeOperations(OpenAPI openApi) {
        if (openApi.getPaths() != null) {
            openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(operation -> {
                    localizeMessage(operation::getSummary, operation::setSummary);
                    localizeMessage(operation::getDescription, operation::setDescription);
                })
            );
        }
    }

    private void localizeMessage(Supplier<String> getter, Consumer<String> setter) {
        String message = getter.get();
        if (message != null) {
            Matcher matcher = MESSAGE_PATTERN.matcher(message);
            if (matcher.matches()) {
                String messageKey = matcher.group(1);
                setter.accept(getMessage(messageKey));
            }
        }
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    private String getMessage(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }
}