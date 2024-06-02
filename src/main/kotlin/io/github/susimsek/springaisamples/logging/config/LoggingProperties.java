package io.github.susimsek.springaisamples.logging.config;

import io.github.susimsek.springaisamples.logging.enums.LogLevel;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "logging.http")
public class LoggingProperties {

    private boolean enabled = true;
    private LogLevel level = LogLevel.BASIC;
    private Obfuscate obfuscate = new Obfuscate();

    @Getter
    @Setter
    public static class Obfuscate {
        private String maskValue = "****";
        private List<String> headers = Arrays.asList(
            "Authorization",
            "Cookie",
            "Set-Cookie",
            "X-API-Key",
            "X-CSRF-Token",
            "WWW-Authenticate"
        );
        private List<String> parameters = Arrays.asList(
            "key",
            "password",
            "token",
            "secret",
            "api_key",
            "access_token",
            "refresh_token"
        );
        private List<String> jsonBodyFields = Arrays.asList(
            "$.password",
            "$.token",
            "$.accessToken",
            "$.refreshToken",
            "$.idToken",
            "$.email",
            "$.secretKey",
            "$.apiSecret",
            "$.apiKey"
        );
    }
}