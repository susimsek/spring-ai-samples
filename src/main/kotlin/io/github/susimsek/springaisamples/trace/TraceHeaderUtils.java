package io.github.susimsek.springaisamples.trace;

import static io.github.susimsek.springaisamples.trace.TraceConstants.CORRELATION_ID_PATTERN_REGEX;
import static io.github.susimsek.springaisamples.trace.TraceConstants.REQUEST_ID_PATTERN_REGEX;

import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

@UtilityClass
public class TraceHeaderUtils {

    public boolean isValidRequestId(String requestId) {
        return StringUtils.hasText(requestId)
            && Pattern.matches(REQUEST_ID_PATTERN_REGEX, requestId);
    }

    public boolean isValidCorrelationId(String correlationId) {
        return StringUtils.hasText(correlationId)
            && Pattern.matches(CORRELATION_ID_PATTERN_REGEX, correlationId);
    }
}