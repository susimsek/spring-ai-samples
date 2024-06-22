package io.github.susimsek.springaisamples.config;

import static io.github.susimsek.springaisamples.idempotency.IdempotencyConstants.IDEMPOTENCY_HEADER_NAME;
import static io.github.susimsek.springaisamples.security.signature.SignatureConstants.JWS_SIGNATURE_HEADER_NAME;
import static io.github.susimsek.springaisamples.trace.TraceConstants.CORRELATION_ID_HEADER_NAME;
import static io.github.susimsek.springaisamples.trace.TraceConstants.REQUEST_ID_HEADER_NAME;

import io.github.susimsek.springaisamples.openapi.LocalizedOpenApiCustomizer;
import io.github.susimsek.springaisamples.openapi.OpenApiProperties;
import io.github.susimsek.springaisamples.openapi.annotation.Idempotent;
import io.github.susimsek.springaisamples.openapi.annotation.RequireJwsSignature;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import java.lang.reflect.Method;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;

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
@EnableConfigurationProperties(OpenApiProperties.class)
public class OpenApiConfig {

    private final OpenApiProperties openApiProperties;

    @Bean
    public OpenApiCustomizer openApiCustomizer(
        MessageSource messageSource) {
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
                .example("tr"));

            operation.addParametersItem(new HeaderParameter()
                .schema(new StringSchema())
                .name(REQUEST_ID_HEADER_NAME)
                .description("Unique request identifier")
                .required(true)
                .example("44fdd967-6d72-43e4-b027-febd9c8ecebc"));

            operation.addParametersItem(new HeaderParameter()
                .schema(new StringSchema())
                .name(CORRELATION_ID_HEADER_NAME)
                .description("Correlation identifier for request")
                .required(true)
                .example("08637921-80c2-4281-ac27-bbf6b7376c4f"));

            if (isIdempotent(handlerMethod.getMethod()) || isIdempotent(handlerMethod.getBeanType())) {
                addIdempotentHeader(operation);
            }

            if (isJwsSignatureRequired(handlerMethod.getMethod()) || isJwsSignatureRequired(handlerMethod.getBeanType())) {
                addJwsSignatureHeader(operation);
            }

            return operation;
        };
    }

    private boolean isIdempotent(Method method) {
        return AnnotatedElementUtils.hasAnnotation(method, Idempotent.class);
    }

    private boolean isIdempotent(Class<?> clazz) {
        return AnnotatedElementUtils.hasAnnotation(clazz, Idempotent.class);
    }

    private boolean isJwsSignatureRequired(Class<?> clazz) {
        return AnnotatedElementUtils.hasAnnotation(clazz, RequireJwsSignature.class);
    }

    private boolean isJwsSignatureRequired(Method method) {
        return AnnotatedElementUtils.hasAnnotation(method, RequireJwsSignature.class);
    }

    private void addIdempotentHeader(Operation operation) {
        Parameter idempotentHeader = new HeaderParameter()
            .schema(new StringSchema())
            .name(IDEMPOTENCY_HEADER_NAME)
            .description("Idempotency Key")
            .required(true)
            .example("831f5ed5-66e7-41bb-9db6-517ffa283b05");
        operation.addParametersItem(idempotentHeader);
    }

    private void addJwsSignatureHeader(Operation operation) {
        Parameter jwsSignatureHeader = new HeaderParameter()
            .schema(new StringSchema())
            .name(JWS_SIGNATURE_HEADER_NAME)
            .description("JWS Signature")
            .required(true);
        operation.addParametersItem(jwsSignatureHeader);
    }
}