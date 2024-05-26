package io.github.susmisek.springaisamples.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import java.util.Arrays;
import lombok.Generated;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

@Configuration(proxyBeanMethods = false)
@OpenAPIDefinition(
    info = @Info(
        title = "Spring AI Samples REST API Documentation",
        description = "Spring AI Samples REST API Documentation",
        version = "v1",
        contact = @Contact(
            name = "Şuayb Şimşek",
            email = "suaybsimsek58@gmail.com",
            url = "https://www.susimsek.github.io"
        ),
        license = @License(
            name = "Apache 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0"
        )
    )
)
@Generated
public class OpenApiConfig {

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