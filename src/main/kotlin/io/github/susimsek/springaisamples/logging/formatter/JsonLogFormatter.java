package io.github.susimsek.springaisamples.logging.formatter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.susimsek.springaisamples.logging.model.HttpLog;
import io.github.susimsek.springaisamples.logging.model.MethodLog;
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

        Optional.ofNullable(httpLog.getDurationMs())
            .ifPresent(duration -> logNode.set("duration", JsonNodeFactory.instance.textNode(
                httpLog.getDurationMs() + "ms")));

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

    @Override
    public String format(MethodLog methodLog) {
        ObjectNode logNode = objectMapper.createObjectNode();
        logNode.set("type", JsonNodeFactory.instance.textNode(methodLog.getType().toString().toLowerCase()));
        logNode.set("className", JsonNodeFactory.instance.textNode(methodLog.getClassName()));
        logNode.set("methodName", JsonNodeFactory.instance.textNode(methodLog.getMethodName()));
        logNode.set("arguments", objectMapper.valueToTree(methodLog.getArguments()));
        Optional.ofNullable(methodLog.getDurationMs())
            .ifPresent(duration -> logNode.set("duration", JsonNodeFactory.instance.textNode(
                methodLog.getDurationMs() + "ms")));

        Optional.ofNullable(methodLog.getResult())
            .ifPresent(result -> logNode.set("result", objectMapper.valueToTree(result)));

        Optional.ofNullable(methodLog.getExceptionMessage())
            .ifPresent(exceptionMessage -> logNode.set("exceptionMessage",
                JsonNodeFactory.instance.textNode(exceptionMessage)));

        Optional.ofNullable(methodLog.getTrace())
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