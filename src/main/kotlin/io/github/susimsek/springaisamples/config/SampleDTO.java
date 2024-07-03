package io.github.susimsek.springaisamples.config;

import io.github.susimsek.springaisamples.validation.TCKN;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app")
@Getter
@Setter
@Validated
@Configuration
public class SampleDTO {

    @TCKN(message = "{validation.field.tckn}")
    private String tckn;
}