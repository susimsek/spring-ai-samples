package io.github.susimsek.springaisamples.logging.formatter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.susimsek.springaisamples.logging.model.HttpLog;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class JsonLogFormatter implements LogFormatter {

    private final ObjectMapper objectMapper;

    @Override
    public String format(HttpLog httpLog) {
        var logNode = objectMapper.createObjectNode()
            .put("source", httpLog.getSource().toString().toLowerCase())
            .put("type", httpLog.getType().toString().toLowerCase())
            .put("method", httpLog.getMethod())
            .put("uri", httpLog.getUri().toString())
            .put("host", httpLog.getUri().getHost())
            .put("path", httpLog.getUri().getPath());

        Optional.ofNullable(httpLog.getStatusCode())
            .ifPresent(statusCode -> logNode.put("statusCode", statusCode));

        Optional.ofNullable(httpLog.getHeaders())
            .ifPresent(headers -> logNode.set("headers", parseHeaders(headers)));

        if (StringUtils.hasText(httpLog.getBody())) {
            logNode.set("body", parseBody(httpLog.getBody()));
        }

        return logNode.toPrettyString();
    }

    private JsonNode parseHeaders(HttpHeaders headers) {
        return objectMapper.valueToTree(headers);
    }

    private JsonNode parseBody(String bodyString) {
        try {
            return objectMapper.readTree(bodyString);
        } catch (IOException e) {
            return objectMapper.createObjectNode().put("body", bodyString); // Not a JSON body, log as plain text
        }
    }
}