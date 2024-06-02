package io.github.susimsek.springaisamples.logging.formatter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.susimsek.springaisamples.logging.model.HttpLog;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;

@RequiredArgsConstructor
public class JsonLogFormatter implements LogFormatter {

    private final ObjectMapper objectMapper;

    @Override
    public String format(HttpLog httpLog) {
        ObjectNode logNode = objectMapper.createObjectNode()
            .put("type", httpLog.getType().toString().toLowerCase())
            .put("method", httpLog.getMethod())
            .put("statusCode", httpLog.getStatusCode());

        Optional.ofNullable(httpLog.getUri()).ifPresent(uri -> logNode
            .put("uri", uri.toString())
            .put("host", uri.getHost()));

        Optional.ofNullable(httpLog.getHeaders())
            .map(this::parseHeaders)
            .ifPresent(headers -> logNode.set("headers", headers));

        Optional.ofNullable(httpLog.getBody())
            .map(this::parseBody)
            .ifPresent(body -> logNode.set("body", body));

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