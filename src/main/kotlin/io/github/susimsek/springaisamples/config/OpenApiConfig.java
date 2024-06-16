package io.github.susimsek.springaisamples.config;

import io.github.susimsek.springaisamples.openapi.LocalizedOpenApiCustomizer;
import io.github.susimsek.springaisamples.openapi.OpenApiProperties;
import io.github.susimsek.springaisamples.security.SignatureConstants;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
@RequiredArgsConstructor
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    in = SecuritySchemeIn.HEADER,
    description = "JWT Authorization header using the Bearer scheme. Example: \"Authorization: Bearer {token}\""
)
@SecurityScheme(
    name = "jwsSignature",
    type = SecuritySchemeType.APIKEY,
    in = SecuritySchemeIn.HEADER,
    paramName = SignatureConstants.JWS_SIGNATURE_HEADER_NAME,
    description = "JWS Signature header using the X-JWS-Signature scheme. Example: \"X-JWS-Signature: {token}\""
)
@EnableConfigurationProperties(OpenApiProperties.class)
public class OpenApiConfig {

    private final OpenApiProperties openApiProperties;

    @Bean
    public OpenApiCustomizer openApiCustomizer(
        MessageSource messageSource,
        RequestMappingHandlerMapping requestMappingHandlerMapping) {
        return new LocalizedOpenApiCustomizer(messageSource, openApiProperties);
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