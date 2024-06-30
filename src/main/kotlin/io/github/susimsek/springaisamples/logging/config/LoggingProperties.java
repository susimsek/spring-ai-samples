package io.github.susimsek.springaisamples.logging.config;

import io.github.susimsek.springaisamples.logging.enums.HttpLogLevel;
import io.github.susimsek.springaisamples.logging.enums.MethodLogLevel;
import java.time.Duration;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

@Getter
@Setter
@ConfigurationProperties(prefix = "logging")
public class LoggingProperties {

    private String pattern = "%clr(%d{yyyy-MM-dd''T''HH:mm:ss.SSSXXX}){faint} %clr(%5p){highlight} "
        + "%clr(${PID:- }){magenta} --- [%clr(${spring.application.name:-}){green},%X{traceId:-},%X{spanId:-},"
        + "%X{requestId:-},%X{correlationId:-}] [%clr(%t){faint}] %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n%clr(%wEx){red}";
    private Http http = new Http();
    private Aspect aspect = new Aspect();
    private Obfuscate obfuscate = new Obfuscate();
    private Async async = new Async();
    private Loki loki = new Loki();

    @Getter
    @Setter
    public static class Http {
        private boolean enabled = true;
        private HttpLogLevel logLevel = HttpLogLevel.BASIC;
    }

    @Getter
    @Setter
    public static class Aspect {
        private boolean enabled = true;
        private MethodLogLevel logLevel = MethodLogLevel.BASIC;
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

    @Getter
    @Setter
    public static class Async {
        private int queueSize = 10000;
        private int discardingThreshold = 0;
        private int maxFlushTime = 1000;
        private boolean includeCallerData = false;
    }

    @Getter
    @Setter
    public static class Loki {
        private  boolean enabled = false;
        private  String url = "http://localhost:3100/loki/api/v1/push";
        private Duration innerThreadsExpiration = Duration.ofMinutes(5);
        private int batchMaxItems = 100;
        private DataSize batchMaxBytes = DataSize.ofMegabytes(1);
        private Duration batchTimeout = Duration.ofSeconds(5);
    }
}