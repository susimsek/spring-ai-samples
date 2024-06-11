package io.github.susimsek.springaisamples.config;

import io.github.susimsek.springaisamples.customizer.OpenApiLocalizationCustomizer;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {

    @Bean
    public OpenApiCustomizer customizeOpenApi(MessageSource messageSource) {
        return new OpenApiLocalizationCustomizer(messageSource);
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