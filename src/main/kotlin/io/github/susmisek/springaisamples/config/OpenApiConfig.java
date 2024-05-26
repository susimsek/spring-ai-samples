package io.github.susmisek.springaisamples.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import lombok.Generated;
import org.springframework.context.annotation.Configuration;

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

}
