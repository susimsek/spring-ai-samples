package io.github.susimsek.springaisamples.logging.formatter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.susimsek.springaisamples.logging.model.HttpLog;
import io.github.susimsek.springaisamples.logging.model.Trace;
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
        ObjectNode logNode = objectMapper.createObjectNode();
        logNode.set("source", JsonNodeFactory.instance.textNode(httpLog.getSource().toString().toLowerCase()));
        logNode.set("type", JsonNodeFactory.instance.textNode(httpLog.getType().toString().toLowerCase()));
        logNode.set("method", JsonNodeFactory.instance.textNode(httpLog.getMethod().name()));
        logNode.set("uri", JsonNodeFactory.instance.textNode(httpLog.getUri().toString()));
        logNode.set("host", JsonNodeFactory.instance.textNode(httpLog.getUri().getHost()));
        logNode.set("path", JsonNodeFactory.instance.textNode(httpLog.getUri().getPath()));

        Optional.ofNullable(httpLog.getStatusCode())
            .ifPresent(statusCode -> logNode.set("statusCode", JsonNodeFactory.instance.numberNode(statusCode)));

        Optional.ofNullable(httpLog.getHeaders())
            .ifPresent(headers -> logNode.set("headers", parseHeaders(headers)));

        if (StringUtils.hasText(httpLog.getBody())) {
            logNode.set("body", parseBody(httpLog.getBody()));
        }

        Optional.ofNullable(httpLog.getTrace())
            .ifPresent(trace -> logNode.set("trace", parseTraceMetadata(trace)));

        return logNode.toPrettyString();
    }

    private JsonNode parseHeaders(HttpHeaders headers) {
        return objectMapper.valueToTree(headers);
    }

    private JsonNode parseBody(String bodyString) {
        try {
            return objectMapper.readTree(bodyString);
        } catch (IOException e) {
            ObjectNode node = objectMapper.createObjectNode();
            node.set("body", JsonNodeFactory.instance.textNode(bodyString)); // Not a JSON body, log as plain text
            return node;
        }
    }

    private JsonNode parseTraceMetadata(Trace trace) {
        ObjectNode traceNode = objectMapper.createObjectNode();
        traceNode.set("traceId", JsonNodeFactory.instance.textNode(trace.getTraceId()));
        traceNode.set("spanId", JsonNodeFactory.instance.textNode(trace.getSpanId()));
        traceNode.set("requestId", JsonNodeFactory.instance.textNode(trace.getRequestId()));
        traceNode.set("correlationId", JsonNodeFactory.instance.textNode(trace.getCorrelationId()));
        return traceNode;
    }
}