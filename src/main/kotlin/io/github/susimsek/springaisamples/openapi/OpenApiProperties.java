package io.github.susimsek.springaisamples.openapi;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "openapi")
public class OpenApiProperties {
    private List<String> priorityTags;
}