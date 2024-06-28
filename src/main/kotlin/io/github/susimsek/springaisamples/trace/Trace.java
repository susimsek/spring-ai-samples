package io.github.susimsek.springaisamples.trace;

import java.util.Objects;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Trace {
    private String traceId;
    private String spanId;
    private String requestId;
    private String correlationId;

    public boolean isComplete() {
        return Stream.of(traceId, spanId, requestId, correlationId).allMatch(Objects::nonNull);
    }
}