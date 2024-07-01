package io.github.susimsek.springaisamples.openapi;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "openapi")
public class OpenApiProperties {

    @NotEmpty(message = "{validation.field.notEmpty}")
    private List<String> priorityTags;
}