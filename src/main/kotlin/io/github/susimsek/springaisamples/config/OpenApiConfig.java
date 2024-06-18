package io.github.susimsek.springaisamples.config;

import static io.github.susimsek.springaisamples.trace.TraceConstants.CORRELATION_ID_HEADER_NAME;
import static io.github.susimsek.springaisamples.trace.TraceConstants.REQUEST_ID_HEADER_NAME;

import io.github.susimsek.springaisamples.openapi.LocalizedOpenApiCustomizer;
import io.github.susimsek.springaisamples.openapi.OpenApiProperties;
import io.github.susimsek.springaisamples.security.SignatureConstants;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
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
            operation.addParametersItem(new HeaderParameter()
                .schema(new StringSchema()._enum(Arrays.asList("en", "tr")))
                .name("Accept-Language")
                .description("Language preference")
                .required(false)
                .schema(new StringSchema())
                .example("tr"));
            operation.addParametersItem(new HeaderParameter()
                .schema(new StringSchema())
                .name(REQUEST_ID_HEADER_NAME)
                .description("Unique request identifier")
                .required(true)
                .example("abcd-1234-efgh-5678"));
            operation.addParametersItem(new HeaderParameter()
                .schema(new StringSchema())
                .name(CORRELATION_ID_HEADER_NAME)
                .description("Correlation identifier for request")
                .required(true)
                .example("ijkl-91011-mnop-1213"));
            return operation;
        };
    }
}