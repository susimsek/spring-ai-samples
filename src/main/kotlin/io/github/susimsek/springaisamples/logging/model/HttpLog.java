package io.github.susimsek.springaisamples.logging.model;

import io.github.susimsek.springaisamples.logging.enums.HttpLogType;
import io.github.susimsek.springaisamples.logging.enums.Source;
import java.net.URI;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpHeaders;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HttpLog {
    private HttpLogType type;
    private String method;
    private URI uri;
    private Integer statusCode;
    private HttpHeaders headers;
    private String body;
    private Source source;
}