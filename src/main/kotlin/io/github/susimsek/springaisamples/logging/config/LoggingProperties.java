package io.github.susimsek.springaisamples.logging.config;

import io.github.susimsek.springaisamples.logging.enums.HttpLogLevel;
import io.github.susimsek.springaisamples.logging.enums.MethodLogLevel;
import io.github.susimsek.springaisamples.validation.DataSizeMax;
import io.github.susimsek.springaisamples.validation.DataSizeMin;
import io.github.susimsek.springaisamples.validation.Enum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;


@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "logging")
public class LoggingProperties {

    @NotBlank(message = "{validation.field.notBlank}")
    private String pattern = "%clr(%d{yyyy-MM-dd''T''HH:mm:ss.SSSXXX}){faint} %clr(%5p){highlight} "
        + "%clr(${PID:- }){magenta} --- [%clr(${spring.application.name:-}){green},%X{traceId:-},%X{spanId:-},"
        + "%X{requestId:-},%X{correlationId:-}] [%clr(%t){faint}] %clr(%-40.40logger{39}){cyan} "
        + "%clr(:){faint} %m%n%clr(%wEx){red}";

    @Valid
    @NotNull(message = "{validation.field.notNull}")
    private Http http = new Http();

    @Valid
    @NotNull(message = "{validation.field.notNull}")
    private Aspect aspect = new Aspect();

    @Valid
    @NotNull(message = "{validation.field.notNull}")
    private Obfuscate obfuscate = new Obfuscate();

    @Valid
    @NotNull(message = "{validation.field.notNull}")
    private Async async = new Async();

    @Valid
    @NotNull(message = "{validation.field.notNull}")
    private Loki loki = new Loki();

    @Getter
    @Setter
    public static class Http {
        private boolean enabled = true;

        @NotNull(message = "{validation.field.notNull}")
        @Enum(enumClass = HttpLogLevel.class, message = "{validation.field.enum}")
        private HttpLogLevel logLevel = HttpLogLevel.BASIC;
    }

    @Getter
    @Setter
    public static class Aspect {
        private boolean enabled = true;

        @NotNull(message = "{validation.field.notNull}")
        @Enum(enumClass = MethodLogLevel.class, message = "{validation.field.enum}")
        private MethodLogLevel logLevel = MethodLogLevel.BASIC;
    }

    @Getter
    @Setter
    public static class Obfuscate {
        private boolean enabled = true;

        @NotBlank(message = "{validation.field.notBlank}")
        private String maskValue = "****";

        private List<String> headers;

        private List<String> parameters;

        private List<String> jsonBodyFields;

        private List<String> methodFields;
    }

    @Getter
    @Setter
    public static class Async {
        @Min(value = 1, message = "{validation.field.min}")
        private int queueSize = 10000;

        @Min(value = 0, message = "{validation.field.min}")
        private int discardingThreshold = 0;

        @Min(value = 1, message = "{validation.field.min}")
        private int maxFlushTime = 1000;

        private boolean includeCallerData = false;
    }

    @Getter
    @Setter
    public static class Loki {
        private boolean enabled = false;

        @NotBlank(message = "{validation.field.notBlank}")
        @URL(message = "{validation.field.url}")
        private String url = "http://localhost:3100/loki/api/v1/push";

        @NotNull(message = "{validation.field.notNull}")
        private Duration innerThreadsExpiration = Duration.ofMinutes(5);

        @Min(value = 1, message = "{validation.field.min}")
        private int batchMaxItems = 100;

        @NotNull(message = "{validation.field.notNull}")
        @DataSizeMin(value = "1MB", message = "{validation.field.min}")
        @DataSizeMax(value = "10MB", message = "{validation.field.max}")
        private DataSize batchMaxBytes = DataSize.ofMegabytes(1);

        @NotNull(message = "{validation.field.notNull}")
        private Duration batchTimeout = Duration.ofSeconds(5);

        @Valid
        @NotNull(message = "{validation.field.notNull}")
        private Retry retry = new Retry();

        private boolean useDirectBuffers = true;
        private boolean drainOnStop = true;
        private boolean metricsEnabled = false;
        private boolean dropRateLimitedBatches = false;
        private boolean verbose = false;

        @NotNull(message = "{validation.field.notNull}")
        private Duration internalQueuesCheckTimeout = Duration.ofMillis(25);

        @Getter
        @Setter
        public static class Retry {
            @Min(value = 1, message = "{validation.field.min}")
            private int maxRetries = 3;

            @NotNull(message = "{validation.field.notNull}")
            private Duration minRetryBackoff = Duration.ofMillis(500);

            @NotNull(message = "{validation.field.notNull}")
            private Duration maxRetryBackoff = Duration.ofMinutes(1);

            @NotNull(message = "{validation.field.notNull}")
            private Duration maxRetryJitter = Duration.ofMillis(500);
        }
    }
}