package io.github.susimsek.springaisamples.logging.config;

import io.github.susimsek.springaisamples.logging.enums.HttpLogLevel;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "logging")
public class LoggingProperties {

    private Http http = new Http();
    private Aspect aspect = new Aspect();
    private Obfuscate obfuscate = new Obfuscate();


    @Getter
    @Setter
    public static class Http {
        private boolean enabled = true;
        private HttpLogLevel level = HttpLogLevel.BASIC;
    }

    @Getter
    @Setter
    public static class Aspect {
        private boolean enabled = true;
    }

    @Getter
    @Setter
    public static class Obfuscate {
        private boolean enabled = true;
        private String maskValue = "****";
        private List<String> headers;
        private List<String> parameters;
        private List<String> jsonBodyFields;
        private List<String> methodFields;
    }
}