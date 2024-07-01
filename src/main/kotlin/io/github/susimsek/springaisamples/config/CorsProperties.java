package io.github.susimsek.springaisamples.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "spring.mvc.cors")
public class CorsProperties {

    private boolean enabled = false;

    @NotEmpty(message = "{validation.field.notEmpty}")
    private List<String> allowedOrigins;

    @NotEmpty(message = "{validation.field.notEmpty}")
    private List<String> allowedMethods;

    @NotEmpty(message = "{validation.field.notEmpty}")
    private List<String> allowedHeaders;

    @NotNull(message = "{validation.field.notNull}")
    private Boolean allowCredentials;

    @NotNull(message = "{validation.field.notNull}")
    @Min(value = 0, message = "{validation.field.min}")
    private Long maxAge;
}