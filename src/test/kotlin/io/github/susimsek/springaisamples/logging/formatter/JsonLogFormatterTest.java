package io.github.susimsek.springaisamples.logging.formatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.susimsek.springaisamples.logging.enums.HttpLogType;
import io.github.susimsek.springaisamples.logging.enums.Source;
import io.github.susimsek.springaisamples.logging.model.HttpLog;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

class JsonLogFormatterTest {

    private JsonLogFormatter jsonLogFormatter;
    private ObjectMapper objectMapper;
    private HttpLog httpLog;

    @BeforeEach
    void setUp() throws URISyntaxException {
        objectMapper = new ObjectMapper();
        jsonLogFormatter = new JsonLogFormatter(objectMapper);

        httpLog = HttpLog.builder()
            .source(Source.CLIENT)
            .type(HttpLogType.REQUEST)
            .method("GET")
            .uri(new URI("https://example.com/path"))
            .statusCode(200)
            .headers(new HttpHeaders())
            .body("plain text body")
            .build();
    }

    @Test
    void testFormat() {
        String formattedLog = jsonLogFormatter.format(httpLog);

        ObjectNode expectedLogNode = objectMapper.createObjectNode()
            .put("source", "client")
            .put("type", "request")
            .put("method", "GET")
            .put("uri", "https://example.com/path")
            .put("host", "example.com")
            .put("path", "/path")
            .put("statusCode", 200);
        expectedLogNode.set("headers", objectMapper.createObjectNode());
        expectedLogNode.set("body", objectMapper.createObjectNode().put("body", "plain text body"));

        String expectedLog = expectedLogNode.toPrettyString();

        assertEquals(expectedLog, formattedLog);
    }

    @Test
    void testFormatWithoutStatusCode() {
        httpLog.setStatusCode(null);

        String formattedLog = jsonLogFormatter.format(httpLog);

        ObjectNode expectedLogNode = objectMapper.createObjectNode()
            .put("source", "client")
            .put("type", "request")
            .put("method", "GET")
            .put("uri", "https://example.com/path")
            .put("host", "example.com")
            .put("path", "/path");
        expectedLogNode.set("headers", objectMapper.createObjectNode());
        expectedLogNode.set("body", objectMapper.createObjectNode().put("body", "plain text body"));

        String expectedLog = expectedLogNode.toPrettyString();

        assertEquals(expectedLog, formattedLog);
    }

    @Test
    void testFormatWithoutBody() {
        httpLog.setBody(null);

        String formattedLog = jsonLogFormatter.format(httpLog);

        ObjectNode expectedLogNode = objectMapper.createObjectNode()
            .put("source", "client")
            .put("type", "request")
            .put("method", "GET")
            .put("uri", "https://example.com/path")
            .put("host", "example.com")
            .put("path", "/path")
            .put("statusCode", 200);
        expectedLogNode.set("headers", objectMapper.createObjectNode());

        String expectedLog = expectedLogNode.toPrettyString();

        assertEquals(expectedLog, formattedLog);
    }

    @Test
    void testFormatNonJsonBody() {
        httpLog.setBody("plain text body");

        String formattedLog = jsonLogFormatter.format(httpLog);

        ObjectNode expectedLogNode = objectMapper.createObjectNode()
            .put("source", "client")
            .put("type", "request")
            .put("method", "GET")
            .put("uri", "https://example.com/path")
            .put("host", "example.com")
            .put("path", "/path")
            .put("statusCode", 200);
        expectedLogNode.set("headers", objectMapper.createObjectNode());
        expectedLogNode.set("body", objectMapper.createObjectNode().put("body", "plain text body"));

        String expectedLog = expectedLogNode.toPrettyString();

        assertEquals(expectedLog, formattedLog);
    }
}