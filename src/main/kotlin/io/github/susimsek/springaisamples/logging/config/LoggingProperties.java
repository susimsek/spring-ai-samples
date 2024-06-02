package io.github.susimsek.springaisamples.logging.config;

import io.github.susimsek.springaisamples.logging.enums.LogLevel;
import io.github.susimsek.springaisamples.logging.model.PathRule;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "logging")
public class LoggingProperties {

    private Http http = new Http();

    @Getter
    @Setter
    public static class Http {
        private boolean enabled = true;
        private LogLevel level = LogLevel.BASIC;
        private Obfuscate obfuscate = new Obfuscate();
        private Exclude exclude = new Exclude();
        private Include include = new Include();
    }

    @Getter
    @Setter
    public static class Obfuscate {
        private boolean enabled = true;
        private String maskValue = "****";
        private List<String> headers;
        private List<String> parameters;
        private List<String> jsonBodyFields;
    }

    @Getter
    @Setter
    public static class Exclude {
        private List<PathRule> rules = Collections.emptyList();
    }

    @Getter
    @Setter
    public static class Include {
        private List<PathRule> rules = List.of(new PathRule("/**", null));
    }
}