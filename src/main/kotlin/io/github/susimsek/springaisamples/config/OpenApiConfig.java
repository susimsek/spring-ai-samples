package io.github.susimsek.springaisamples.config;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import java.util.Arrays;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.method.HandlerMethod;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenApiCustomizer customizeOpenApi(MessageSource messageSource) {
        return openApi -> {
            var locale = LocaleContextHolder.getLocale();
            openApi.getInfo()
                .title(messageSource.getMessage("api-docs.title", null, locale))
                .description(messageSource.getMessage("api-docs.description", null, locale))
                .contact(new io.swagger.v3.oas.models.info.Contact()
                    .name(messageSource.getMessage("api-docs.contact.name", null, locale))
                    .email(messageSource.getMessage("api-docs.contact.email", null, locale))
                    .url(messageSource.getMessage("api-docs.contact.url", null, locale)))
                .license(new io.swagger.v3.oas.models.info.License()
                    .name(messageSource.getMessage("api-docs.license.name", null, locale))
                    .url(messageSource.getMessage("api-docs.license.url", null, locale)));
        };
    }

    @Bean
    public OperationCustomizer operationCustomizer() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            Parameter acceptLanguageHeader = new Parameter()
                .in("header")
                .schema(new StringSchema()._enum(Arrays.asList("en", "tr")))
                .name("Accept-Language")
                .description("Language preference")
                .required(false);
            operation.addParametersItem(acceptLanguageHeader);
            return operation;
        };
    }
}