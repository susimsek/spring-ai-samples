package io.github.susimsek.springaisamples.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@RequiredArgsConstructor
@Slf4j
public class LocalizedOpenApiCustomizer implements OpenApiCustomizer {

    private static final Pattern MESSAGE_PATTERN = Pattern.compile("\\{(.+?)}");
    private final MessageSource messageSource;
    private final OpenApiProperties openApiProperties;

    @Override
    public void customise(OpenAPI openApi) {
        var locale = LocaleContextHolder.getLocale();
        customiseOpenApiInfo(openApi, locale);
        customiseOperations(openApi);
        sortTags(openApi);
        addSecurityScheme(openApi);
    }

    private void customiseOpenApiInfo(OpenAPI openApi, Locale locale) {
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

    private void customiseOperations(OpenAPI openApi) {
        if (openApi.getPaths() != null) {
            openApi.getPaths().forEach((path, pathItem) ->
                pathItem.readOperations().forEach(operation -> {
                    localiseMessage(operation::getSummary, operation::setSummary);
                    localiseMessage(operation::getDescription, operation::setDescription);
                })
            );
        }
    }

    private void localiseMessage(Supplier<String> getter, Consumer<String> setter) {
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

    private void sortTags(OpenAPI openApi) {
        if (openApi.getTags() != null) {
            List<String> priorityTags = openApiProperties.getPriorityTags();
            openApi.getTags().sort((tag1, tag2) -> {
                int index1 = priorityTags.indexOf(tag1.getName());
                int index2 = priorityTags.indexOf(tag2.getName());

                if (index1 == -1 && index2 == -1) {
                    return tag1.getName().compareTo(tag2.getName());
                }
                if (index1 == -1) {
                    return 1;
                }
                if (index2 == -1) {
                    return -1;
                }
                return Integer.compare(index1, index2);
            });
        }
    }

    private void addSecurityScheme(OpenAPI openApi) {
        openApi.getComponents()
            .addSecuritySchemes("bearerAuth",
                new io.swagger.v3.oas.models.security.SecurityScheme()
                    .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .in(io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER)
                    .description(
                        "JWT Authorization header using the Bearer scheme. "
                            + "Example: \"Authorization: Bearer {token}\""));
    }
}